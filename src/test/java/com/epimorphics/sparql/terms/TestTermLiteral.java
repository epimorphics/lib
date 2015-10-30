/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import static org.junit.Assert.*;

import org.junit.Test;

import static com.epimorphics.test.utils.Asserts.*;

public class TestTermLiteral {
	
	@Test public void testTermLiteral() {
		String lexical = "abc123";
		
		URI type = new URI("http://example.com/type-typical");
		URI type2 = new URI("http://example.com/type-typical2");
		Literal lA = new Literal(lexical, type, "");
		
		assertEquals(lexical, lA.getLexicalForm());
		assertEquals("", lA.getLanguage());
		assertEquals(type, lA.getLiteralType());
		
		Literal given = new Literal(lexical, type, "fr");
		assertEquals(given, new Literal(lexical, type, "fr"));
		
		assertDiffer(given, new Literal("lexical", type, "fr"));
		assertDiffer(given, new Literal(lexical, type2, "fr"));
		assertDiffer(given, new Literal(lexical, type, "en"));
		
		assertEquals(given.hashCode(), given.spelling.hashCode() + given.getLanguage().hashCode() + given.getLiteralType().hashCode());
	}
}
