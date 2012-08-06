/******************************************************************
 * File:        PrefixUtils.java
 * Created by:  Dave Reynolds
 * Created on:  19 Mar 2012
 *
 * (c) Copyright 2012, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.shared.PrefixMapping;

/**
 * Collection of random utilities for working with prefixes.
 *
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class PrefixUtils {

    /**
     * Return a read-only merge of two prefix mappings
     */
    public static PrefixMapping merge(PrefixMapping pm1, PrefixMapping pm2) {
        return new MergePrefixMapping(pm1, pm2);
    }

    static class MergePrefixMapping implements PrefixMapping {
        PrefixMapping pm1;
        PrefixMapping pm2;
        public MergePrefixMapping(PrefixMapping pm1, PrefixMapping pm2) {
            this.pm1 = pm1;
            this.pm2 = pm2;
        }

        @Override
        public PrefixMapping setNsPrefix(String prefix, String uri) {
            throw new JenaLockedException(this);
        }
        @Override
        public PrefixMapping removeNsPrefix(String prefix) {
            throw new JenaLockedException(this);
        }
        @Override
        public PrefixMapping setNsPrefixes(PrefixMapping other) {
            throw new JenaLockedException(this);
        }
        @Override
        public PrefixMapping setNsPrefixes(Map<String, String> map) {
            throw new JenaLockedException(this);
        }
        @Override
        public PrefixMapping withDefaultMappings(PrefixMapping map) {
            throw new JenaLockedException(this);
        }
        @Override
        public String getNsPrefixURI(String prefix) {
            String result = pm1.getNsPrefixURI(prefix);
            return result == null ? pm2.getNsPrefixURI(prefix) : result;
        }
        @Override
        public String getNsURIPrefix(String uri) {
            String result = pm1.getNsURIPrefix(uri);
            return result == null ? pm2.getNsURIPrefix(uri) : result;
        }
        @Override
        public Map<String, String> getNsPrefixMap() {
            Map<String, String>  result = pm1.getNsPrefixMap();
            result.putAll( pm2.getNsPrefixMap() );
            return result;
        }
        @Override
        public String expandPrefix(String prefixed) {
            return pm1.expandPrefix( pm2.expandPrefix(prefixed) );
        }
        @Override
        public String shortForm(String uri) {
            return pm1.shortForm( pm2.shortForm(uri) );
        }
        @Override
        public String qnameFor(String uri) {
            String result = pm1.qnameFor(uri);
            return result == null ? pm2.qnameFor(uri) : result;
        }
        @Override
        public PrefixMapping lock() {
            return this;
        }
        @Override
        public boolean samePrefixMappingAs(PrefixMapping other) {
            return false;
        }
    }

    /**
     * Expand a SPARQL query by prefixing it will all possibly relevant
     * mappings from the given prefix mapping
     */
    public static String expandQuery(String query, PrefixMapping pm) {
        Set<String> prefixes = findPrefixes(query);
        StringBuilder result = new StringBuilder();
        for (String prefix : prefixes) {
            String uri = pm.getNsPrefixURI(prefix);
            if (uri != null) {
                result.append("PREFIX " + prefix + ": <" + uri + ">\n");
            }
        }
        result.append(query);
        return result.toString();
    }

    private final static Pattern prefixPatt = Pattern.compile("([a-zA-Z0-9-_][a-zA-Z0-9-_\\.]*):");

    private static Set<String> findPrefixes(String source) {
        Set<String> prefixes = new HashSet<String>();
        Matcher matcher = prefixPatt.matcher(source);
        while (matcher.find()) {
            prefixes.add( matcher.group(1) );
        }
        return prefixes;
    }

}
