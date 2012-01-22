/******************************************************************
 * File:        NameUtils.java
 * Created by:  Dave Reynolds
 * Created on:  10 Apr 2011
 * 
 * (c) Copyright 2011, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.util;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import com.hp.hpl.jena.iri.IRI;
import com.hp.hpl.jena.iri.IRIFactory;

/**
 * Utilities for checking and converting names to forms safely
 * usable in different circumstances.
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class NameUtils {

    /**
     * Convert an arbitrary name to name safe to use as a file name or URL path slug.
     * Not reversible. 
     */
	public static String safeName(String name) {
        return name.replaceAll("[^@a-zA-Z0-9_\\.\\-~]+", "_");
	}
	
	/**
	 * Test if the given name is a legal URI. 
	 */
	public static boolean isURI(String name) {
        IRI testing = IRIFactory.jenaImplementation().create(name);
        return ! testing.hasViolation(false);
	}
	
	/**
	 * Convert a name to a safe name in a reversible fashion by
	 * (very) conservative percent-encoding of the UFT-8 version. 
	 */
	public static String encodeSafeName(String name) {
	    try {
            StringBuilder encode = new StringBuilder();
            for (byte b : name.getBytes("UTF-8")) {
                char c = (char)b;
                if (c == '_' || c == '-' | c == '@' | c == '.' | Character.isLetterOrDigit(c)) {
                    encode.append(c);
                } else {
                    encode.append("%");
                    encode.append( Integer.toHexString(b) );
                }
            }
            return encode.toString();
        } catch (UnsupportedEncodingException e) {
            throw new EpiException(e);
        }
	}

	/**
	 * Decode an encoded safe name
	 */
	public static String decodeSafeName(String name) {
	    byte[] decode = new byte[ name.length() ];
	    int p = 0;
	    for (int i = 0; i < name.length(); i++) {
	    	char c = name.charAt(i);
	    	if (c == '%') {
	    		decode[p++] = (byte) Integer.parseInt(name.substring(i+1, i+3), 16);
	    		i += 2;
	    	} else {
	    		decode[p++] = (byte) c;
	    	}
	    }
	    byte[] after = new byte[p];
	    System.arraycopy(decode, 0, after, 0, p);
	    try {
            return new String(after, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new EpiException(e);
        }
	}
	
    /**
     * Generate a random name for something of the given type, result
     * will currently look like: {type}-{uuid}
     */
    public static String newName(String type){
        return type + "-" + UUID.randomUUID();
    }
    
   /**
    * Extract an integer from a parameter object, with default
    */
    public static int asInt(Object param, int defaultint) {
        if (param == null) { 
            return defaultint;
        }
        if (param instanceof Number) {
            return ((Number)param).intValue();
        }
        try {
            return Integer.parseInt(param.toString());
        } catch (NumberFormatException e) {
            return defaultint;
        }
    }
    
    /**
     * Normalize a relative file path to unix syntax.
     * I.e. on widows will replace "\" by "/"
     */
    public static String normalizeFilepath(String path) {
        // Note: Using hardwired "\" rather than File.separator so
        // that the normalization will happen even if we are running on UNIX
        // but have been given a parameter generated while on windows
        return path.replaceAll("\\\\", "/");
    }
}
