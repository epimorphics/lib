/******************************************************************
 * File:        PrefixUtils.java
 * Created by:  Dave Reynolds
 * Created on:  19 Mar 2012
 *
 * (c) Copyright 2012, Epimorphics Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *****************************************************************/

package com.epimorphics.util;

import java.io.StringWriter;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.epimorphics.vocabs.*;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;
import com.hp.hpl.jena.vocabulary.DCTerms;

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
        if (pm2 == null) return pm1;
        if (pm1 == null) return pm2;
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

    /**
     * Return a set of commonly used RDF prefixes. This includes the Jena standard
     * mapping (<code>rdf:</code>, <code>owl:</code>, etc) and prefixes for all of the
     * vocabularies defined in epimorphics-lib.
     *
     * @return A prefix mapping object
     * @see #commonPrefixes(PrefixMapping)
     */
    public static PrefixMapping commonPrefixes() {
        return commonPrefixes( (PrefixMapping) null );
    }

    /**
     * Utility for easily declaring a prefix mapping in code. The arguments are
     * assumed to be pairs of strings, alternating prefix and URI.
     * @param declarations Alternating prefix and URI strings
     * @return A new {@link PrefixMapping} containing only the prefixes declared
     * @exception EpiException if the declarations are not in pairs
     */
    public static PrefixMapping asPrefixes( String... declarations ) {
        PrefixMapping pm = new PrefixMappingImpl();

        try {
            for (int i = 0; i < declarations.length; i += 2) {
                pm.setNsPrefix( declarations[i], declarations[i+1] );
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new EpiException( "Prefix without a corresponding URI", e );
        }

        return pm;
    }

    /**
     * Return a set of commonly used RDF prefixes. This includes the Jena standard
     * mapping (<code>rdf:</code>, <code>owl:</code>, etc) and prefixes for all of the
     * vocabularies defined in epimorphics-lib. This variant also allows additional
     * prefixes to be declared in code as alternating prefix and uri strings.
     *
     * @param additional Array of String objects denoting, alternately, a prefix
     * and a URI
     * @return A prefix mapping object
     * @see #commonPrefixes(PrefixMapping)
     */
    public static PrefixMapping commonPrefixes( String... additional ) {
        return commonPrefixes( asPrefixes( additional ) );
    }

    /**
     * Return a set of commonly used RDF prefixes. This includes the Jena standard
     * mapping (<code>rdf:</code>, <code>owl:</code>, etc) and prefixes for all of the
     * vocabularies defined in epimorphics-lib.
     *
     * @param additional Optional additional prefixes to merge into the common core, or null
     * @return A prefix mapping object
     */
    public static PrefixMapping commonPrefixes( PrefixMapping additional ) {
        PrefixMapping pm = new PrefixMappingImpl();
        pm.setNsPrefixes( PrefixMapping.Standard );

        // local Epimorphics library prefixes
        pm.setNsPrefix( "api", API.getURI() );
        pm.setNsPrefix( "qb", Cube.getURI() );
        pm.setNsPrefix( "dgu", DGU.getURI() );
        pm.setNsPrefix( "internal", Internal.getURI() );
        pm.setNsPrefix( "opensearch", OpenSearch.getURI() );
        pm.setNsPrefix( "record", Record.getURI() );
        pm.setNsPrefix( "skos", SKOS.getURI() );
        pm.setNsPrefix( "time", Time.getURI() );
        pm.setNsPrefix( "void", VOID.getURI() );
        pm.setNsPrefix( "xhv", XHV.getURI() );
        pm.setNsPrefix( "dct", DCTerms.getURI() );

        // merge given prefixes
        if (additional != null) {
            pm.setNsPrefixes( additional );
        }

        return pm;
    }

    /**
     * Return the contents of the given prefix mapping, formatted for prepending
     * onto Turtle content.
     * @param pm
     * @return
     */
    public static String asTurtlePrefixes( PrefixMapping pm ) {
        return asPrefixString( pm, true );
    }

    /**
     * Return the contents of the given prefix mapping, formatted for prepending
     * onto a SPARQL query.
     * @param pm
     * @return
     */
    public static String asSparqlPrefixes( PrefixMapping pm ) {
        return asPrefixString( pm, false );
    }

    public static String asPrefixString( PrefixMapping pm, boolean turtle ) {
        List<Entry<String,String>> entries = new ArrayList<Entry<String,String>>();
        entries.addAll( pm.getNsPrefixMap().entrySet() );

        Collections.sort( entries, new Comparator<Entry<String,String>>() {
            @Override
            public int compare(Map.Entry<String,String> o1, Map.Entry<String,String> o2) {
                return o1.getKey().compareTo( o2.getKey() );
            }
        } );

        StringWriter buf = new StringWriter();
        for (Entry<String,String> entry: entries) {
            if (turtle) {buf.append( "@" );}
            buf.append( "prefix " );
            buf.append( entry.getKey() );
            buf.append( ": <" );
            buf.append( entry.getValue() );
            buf.append( ">" );
            if (turtle) {buf.append( "." );}
            buf.append( "\n" );
        }

        return buf.toString();
    }
}
