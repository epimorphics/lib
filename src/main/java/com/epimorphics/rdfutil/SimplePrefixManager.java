/******************************************************************
 * File:        DefaultPrefixManager.java
 * Created by:  Dave Reynolds
 * Created on:  18 Apr 2011
 * 
 * (c) Copyright 2011, Epimorphics Limited
 * $Id:  $
 *****************************************************************/

package com.epimorphics.rdfutil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.FileManager;


/**
 * Version of prefix manager where the shared prefixes have to
 * be either explicitly registered or loaded from a bootstrap models
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 * @version $Revision: $
 */
public class SimplePrefixManager implements PrefixManager {

    // state
    protected final Map<String, String> prefixToURI = new HashMap<String, String>();
    protected final Map<String, String> URItoPrefix = new HashMap<String, String>();

    @Override
    public synchronized void registerPrefix(String prefix, String uri) {
        prefixToURI.put(prefix, uri);
        URItoPrefix.put(uri, prefix);
    }

    /**
     * Registers prefixes from the given RDF file. 
     */
    public void registerFromFile(String prefixFile) {
        Model prefixMOdel = FileManager.get().loadModel(prefixFile);
        registerPrefixes(prefixMOdel);
    }

    @Override
    public synchronized void registerPrefixes(PrefixMapping mapping) {
        for (Map.Entry<String, String> map : mapping.getNsPrefixMap().entrySet()) {
            registerPrefix(map.getKey(), map.getValue());
        }
    }
    
    @Override
    public synchronized String expand(String curie) {
        Matcher matcher = prefixPatt.matcher(curie);
        if (matcher.find()) {
            if (matcher.start() == 0) {
                String prefix = matcher.group(1);
                String exp = prefixToURI.get(prefix);
                if (exp != null) return exp + curie.substring(matcher.end());
            }
        }
        // Try script-friendly version as well
        int split = curie.indexOf('_');
        if (split != -1) {
            String prefix = curie.substring(0, split);
            String exp = prefixToURI.get(prefix);
            if (exp != null) return exp + curie.substring(split+1);
        }
        // TODO add support for dynamic call out to prefix.cc
        return curie;
    }
    
    private final Pattern prefixPatt = Pattern.compile("([a-zA-Z0-9-][a-zA-Z0-9-\\.]*):");
    
    @Override
    public synchronized String expandQuery(String query) {
        return expandQuery(query, null);
    }

    @Override
    public String expandQuery(String query, PrefixMapping baseMapping) {
        Set<String> prefixes = findPrefixes(query);
        StringBuilder result = new StringBuilder();
        for (String prefix : prefixes) {
            String uri = prefixToURI.get(prefix);
            if (uri == null && baseMapping != null) {
                uri = baseMapping.getNsPrefixURI(prefix);
            }
            if (uri != null) {
                result.append("PREFIX " + prefix + ": <" + uri + ">\n");
            }
        }
        result.append(query);
        return result.toString();
    }

    private Set<String> findPrefixes(String source) {
        Set<String> prefixes = new HashSet<String>();
        Matcher matcher = prefixPatt.matcher(source);
        while (matcher.find()) {
            prefixes.add( matcher.group(1) );
        }
        return prefixes;
    }

    @Override
    // This implementation, like Jena's implementation does a brute force
    // search to avoid relying on localName algorithm and so allows
    // non-NCNAME local names
    public synchronized String shorten(String uri) {
        for (Entry<String, String> mapping : prefixToURI.entrySet()) {
            String prefURI = mapping.getValue();
            if (uri.startsWith(prefURI)) {
                String prefix = mapping.getKey();
                return prefix + ":" + uri.substring(prefURI.length());
            }
        }
        return uri;
    }

    @Override
    public synchronized boolean isKnown(String curie) {
        Matcher matcher = prefixPatt.matcher(curie);
        if (matcher.find()) {
            if (matcher.start() == 0) {
                String prefix = matcher.group(1);
                return prefixToURI.containsKey(prefix);
            }
        }
        return false;
    }

    @Override
    public String scriptShorten(String uri) {
        for (Entry<String, String> mapping : prefixToURI.entrySet()) {
            String prefURI = mapping.getValue();
            if (uri.startsWith(prefURI)) {
                String prefix = mapping.getKey();
                return prefix + "_" + uri.substring(prefURI.length());
            }
        }
        return uri;
    }

    @Override
    public String prefixHeader(String turtle, PrefixMapping baseMapping) {
        Set<String> prefixes = findPrefixes(turtle);
        StringBuilder result = new StringBuilder();
        for (String prefix : prefixes) {
            String uri = null;
            if (baseMapping != null) {
                uri = baseMapping.getNsPrefixURI(prefix);
            }
            if (uri == null) {
                uri = prefixToURI.get(prefix);
            }
            if (uri != null) {
                result.append("@prefix " + prefix + ": <" + uri + ">.\n");
            }
        }
        return result.toString();
    }
    
}

