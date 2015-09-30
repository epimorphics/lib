/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import static org.junit.Assert.*;

import org.junit.Test;

import static com.epimorphics.test.utils.Asserts.*;

public class TestTermLiteral {
	
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
}
