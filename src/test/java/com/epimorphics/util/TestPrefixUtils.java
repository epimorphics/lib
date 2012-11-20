/******************************************************************
 * File:        TestPrefixUtils.java
 * Created by:  Dave Reynolds
 * Created on:  19 Mar 2012
 *
 * (c) Copyright 2012, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.epimorphics.vocabs.Cube;
import com.hp.hpl.jena.shared.PrefixMapping;

public class TestPrefixUtils {

    PrefixMapping pm1;
    PrefixMapping pm2;

    @Before
    public void init() {
        pm1 = PrefixMapping.Factory.create();
        pm1.setNsPrefix("p_a", "http://prefix/a#");
        pm1.setNsPrefix("p-b", "http://prefix/b#");

        pm2 = PrefixMapping.Factory.create();
        pm2.setNsPrefix("pc", "http://prefix/c#");
    }

    @Test
    public void testPrefixMerge() {
        PrefixMapping pm = PrefixUtils.merge(pm1, pm2);
        assertEquals("http://prefix/a#", pm.getNsPrefixURI("p_a"));
        assertEquals("http://prefix/b#", pm.getNsPrefixURI("p-b"));
        assertEquals("http://prefix/c#", pm.getNsPrefixURI("pc"));

        assertEquals("http://prefix/a#foo", pm.expandPrefix("p_a:foo"));
        assertEquals("http://prefix/c#foo", pm.expandPrefix("pc:foo"));

        assertEquals("p_a:foo", pm.qnameFor("http://prefix/a#foo"));
        assertEquals("pc:foo", pm.qnameFor("http://prefix/c#foo"));
        assertNull( pm.qnameFor("http://prefix/d#foo") );
    }

    @Test
    public void testExpandQuery() {
        String query = "SELECT * WHERE { p_a:i pc:prop <http://example.com>; p-b:bar []}";
        String result = PrefixUtils.expandQuery(query, PrefixUtils.merge(pm1, pm2));
        assertTrue( result.contains("PREFIX p_a: <http://prefix/a#>") );
        assertTrue( result.contains("PREFIX p-b: <http://prefix/b#>") );
        assertTrue( result.contains("PREFIX pc: <http://prefix/c#>") );
        assertTrue( result.endsWith(query) );
    }

    @Test
    public void testCommonPrefixes() {
        assertNotNull( PrefixUtils.commonPrefixes() );
        assertEquals( Cube.getURI(), PrefixUtils.commonPrefixes().getNsPrefixURI( "qb" ));
    }

    @Test
    public void testCommonPrefixesWithAdditional() {
        PrefixMapping pm = PrefixUtils.commonPrefixes( "unittest", "http://example.test/fu/bar#" );
        assertNotNull( pm );
        assertEquals( Cube.getURI(), pm.getNsPrefixURI( "qb" ));
        assertEquals( "http://example.test/fu/bar#", pm.getNsPrefixURI( "unittest" ));
    }

    @Test
    public void testDeclarePrefixes() {
        PrefixMapping pm = PrefixUtils.asPrefixes( "test1", "foobar" );
        assertEquals( "foobar", pm.getNsPrefixURI( "test1" ));

        pm = PrefixUtils.asPrefixes( "test1", "foobar", "test2", "bufar" );
        assertEquals( "foobar", pm.getNsPrefixURI( "test1" ));
        assertEquals( "bufar", pm.getNsPrefixURI( "test2" ));
    }

    @Test
    public void testAsSparqlPrefixes() {
        String prefixes = PrefixUtils.asSparqlPrefixes( PrefixUtils.commonPrefixes() );
        assertTrue( prefixes.startsWith( "prefix api: <http://purl.org/linked-data/api/vocab#>\n" ));
    }

    @Test
    public void testAsTurtlePrefixes() {
        String prefixes = PrefixUtils.asTurtlePrefixes( PrefixUtils.commonPrefixes() );
        assertTrue( prefixes.startsWith( "@prefix api: <http://purl.org/linked-data/api/vocab#>.\n" ));
    }
}
