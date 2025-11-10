/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import static com.epimorphics.util.Asserts.assertDiffer;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.XSD;
import org.junit.jupiter.api.Test;

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
		
        Literal fn = Literal.fromRDFNode( ResourceFactory.createTypedLiteral(lexical, XSDDatatype.XSDstring) );
        assertEquals(lexical, fn.getLexicalForm());
        assertEquals(XSD.xstring.getURI(), fn.getLiteralType().getURI());
	}
}
