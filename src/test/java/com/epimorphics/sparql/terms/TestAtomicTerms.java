/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestAtomicTerms {
	
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
	
	@Test public void testTermLiteral() {
		String lexical = "abc123";
		
		TermURI type = new TermURI("http://example.com/type-typical");
		TermURI type2 = new TermURI("http://example.com/type-typical2");
		TermLiteral lA = new TermLiteral(lexical, type, "");
		
		assertEquals(lexical, lA.getSpelling());
		assertEquals(lexical, lA.getLexicalForm());
		assertEquals("", lA.getLanguage());
		assertEquals(type, lA.getLiteralType());
		
		TermLiteral given = new TermLiteral(lexical, type, "fr");
		assertEquals(given, new TermLiteral(lexical, type, "fr"));
		
		assertDiffer(given, new TermLiteral("lexical", type, "fr"));
		assertDiffer(given, new TermLiteral(lexical, type2, "fr"));
		assertDiffer(given, new TermLiteral(lexical, type, "en"));
		
		assertEquals(given.hashCode(), given.spelling.hashCode() + given.getLanguage().hashCode() + given.getLiteralType().hashCode());
	}

	private void assertDiffer(Object expected, Object actual) {
		if (expected.equals(actual)) {
			fail("actual should be other than '" + expected + "'");
		}
	}

}
