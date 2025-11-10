/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.util.SparqlUtils;

public class TestTermURI {
	
	@Test public void testTermURI() {
		String spellingA = "http://example.com/term-uri-a";
		String spellingB = "http://example.com/term-uri-b";
		URI tuA = new URI(spellingA);
		URI tuB = new URI(spellingB);
		
		assertTrue(tuA instanceof TermAtomic);
		
		assertEquals(tuA, new URI(spellingA));
		assertFalse(tuA.equals(tuB));
		
		assertEquals(tuA.hashCode(), new URI(spellingA).hashCode());
		assertFalse(tuA.hashCode() == tuB.hashCode());
		
		assertEquals(spellingA, tuA.getURI());
		assertEquals(spellingB, tuB.getURI());
		
		assertEquals("<"+spellingA+">", tuA.toString());
	}
	
	@Test public void testSubstPrefixes() {
		Settings s = new Settings();
		s.setPrefix("ex", "http://example.com/");
		
		URI tu = new URI("http://example.com/alpha");
		String obtained = SparqlUtils.renderToSparql(s, tu);
		
		assertEquals("ex:alpha", obtained);
		
		assertTrue(s.getUsedPrefixes().contains("ex"), "usedPrefixes must contain used prefix");
	}
}
