/*
    See lda-top/LICENCE (or https://raw.github.com/epimorphics/elda/master/LICENCE)
    for the licence for this software.
    
    (c) Copyright 2011 Epimorphics Limited
    $Id$
*/

package com.epimorphics.test.utils;

import java.util.*;

/**
    Helper code to construct String->String hash maps from tiny strings.
 
 	@author chris
*/
public class MakeHash {
	/**
	    Answer a hashmap based on the bindings: k1=v1 k2=v2 ...
	*/
	public static Map<String, String> hashMap( String bindings ) { 
		return hashMap( bindings, " +" ); 
	}

	/**
	    Answer a hashmap based on the bindings: k1=v1 snip k2=v2 ...
	*/
	public static Map<String, String> hashMap( String bindings, String snip ) {
		Map<String, String> result = new HashMap<String, String>();
		if (bindings.length() > 0)
			for (String b: bindings.split( snip )) {
				String [] parts = b.split( "=" );
				result.put( parts[0], parts[1] );
			}
		return result;
	}
}
