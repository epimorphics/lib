/******************************************************************
 * File:        TestPrefixManager.java
 * Created by:  Dave Reynolds
 * Created on:  18 Apr 2011
 * 
 * (c) Copyright 2011, Epimorphics Limited
 * $Id:  $
 *****************************************************************/

package com.epimorphics.rdfutil;

import junit.framework.TestCase;

public class TestPrefixManager extends TestCase {

    public void testBasicOperation() {
        PrefixManager pm = new SimplePrefixManager();
        pm.registerFromFile("src/main/resources/prefixes.ttl");

        
        assertEquals("http://www.w3.org/2000/01/rdf-schema#label", pm.expand("rdfs:label"));
        assertEquals("http://www.w3.org/2000/01/rdf-schema#label", pm.expand("rdfs_label"));
        assertEquals("http://example.com", pm.expand("http://example.com"));
        assertEquals("foo:bar", pm.expand("foo:bar"));
        
        String query = "SELECT ?x, ?y WHERE {?x rdf:type rdf:List ; rdfs:label ?y}";
        String expected = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
        		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
        		query;
        assertEquals(expected, pm.expandQuery(query) );
        
        assertEquals("rdfs:label", pm.shorten( "http://www.w3.org/2000/01/rdf-schema#label") );
        assertEquals("rdfs:123", pm.shorten( "http://www.w3.org/2000/01/rdf-schema#123" )); 
        assertEquals("rdfs_123", pm.scriptShorten( "http://www.w3.org/2000/01/rdf-schema#123" )); 
    }
}

