/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.epimorphics.sparql.templates.Settings;

public class TestTermURI {
	
	@Test public void testTermURI() {
		String spellingA = "http://example.com/term-uri-a";
		String spellingB = "http://example.com/term-uri-b";
		TermURI tuA = new TermURI(spellingA);
		TermURI tuB = new TermURI(spellingB);
		
		assertTrue(tuA instanceof TermAtomic);
		
		assertEquals(tuA, new TermURI(spellingA));
		assertFalse(tuA.equals(tuB));
		
		assertEquals(tuA.hashCode(), new TermURI(spellingA).hashCode());
		assertFalse(tuA.hashCode() == tuB.hashCode());
		
		assertEquals(spellingA, tuA.getURI());
		assertEquals(spellingB, tuB.getURI());

		assertEquals(spellingA, tuA.getSpelling());
		assertEquals(spellingB, tuB.getSpelling());
		
		assertEquals("<"+spellingA+">", tuA.toString());
	}
	
	@Test public void testSubstPrefixes() {
		Settings s = new Settings();
		s.setPrefix("ex", "http://example.com/");
		
		TermURI tu = new TermURI("http://example.com/alpha");
		StringBuilder sb = new StringBuilder();
		tu.toSparql(s, sb);
		assertEquals("ex:alpha", sb.toString());
	}
}
