/******************************************************************
 * File:        TestPrefixUtils.java
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

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.epimorphics.util.PrefixUtils.MergePrefixMapping;
import com.epimorphics.vocabs.Cube;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;

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

    protected static class LoggingMergeMapping extends MergePrefixMapping {
    	
    	protected final Set<String> allowed = new HashSet<String>();
    	protected final Set<String> seen = new HashSet<String>();
    	
    	protected LoggingMergeMapping(PrefixMapping pm1, PrefixMapping pm2, String... ok) {
    		super(pm1, pm2);
    		for (String o: ok) allowed.add(o);
    	}
    	
    	@Override public String getNsPrefixURI(String prefix) {
    		assertTrue("incorrect prefix '" + prefix + "' supplied", allowed.contains(prefix));
    		seen.add(prefix);
    		return super.getNsPrefixURI(prefix);
    	}
    	
    	protected void check() {
    		assertEquals("", allowed, seen);
    	}
    }
    
    @Test 
    public void testingLegalPrefixes() {
    	testLegalPrefixes("Select p_a:x", "p_a");
    	testLegalPrefixes("Select -p_a:x", "p_a");
    	testLegalPrefixes("Select !p_a:x", "p_a");
    	testLegalPrefixes("Select _p_a:x", "p_a");
    	testLegalPrefixes("Select .p_a:x", "p_a");
    	testLegalPrefixes("Select 9p_a:x", "p_a");
    	testLegalPrefixes("Select 9p_a:x", "p_a");

    	testLegalPrefixes("Select p_a_:x", "p_a_");
    	testLegalPrefixes("Select p_a-:x", "p_a-");
    	testLegalPrefixes("Select p_a9:x", "p_a9");
    	testLegalPrefixes("Select p_a.b:x", "p_a.b");  	
    	testLegalPrefixes("Select p_a.:x"); 
    	
    }
    
    /**
    	Test that when the supplied query (any string) is expanded,
    	all and only the expected prefixes are encountered to be
    	prefixed.
    */
    private void testLegalPrefixes(String query, String ...expecting) {
    	LoggingMergeMapping pm = new LoggingMergeMapping
            (pm1, pm2, expecting);

		String result = PrefixUtils.expandQuery(query, pm);
		assertTrue(result.endsWith(query));
    	pm.check();
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
