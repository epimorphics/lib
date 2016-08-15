/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.templates;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.epimorphics.test.utils.MakeCollection;

public class TestSparqlTemplateSyntax {

	protected Element literal(String spelling) {
		return new PlainText(spelling);
	}
	
	protected Element param(String spelling) {
		return param(spelling, Parameter.USUAL);
	}
	
	protected Element param(String spelling, String type) {
		return new Parameter(spelling, type);
	}
	
	@Test public void testSingleLiteral() {
		Template t = new Template("SELECT ?x WHERE {}");
		assertEquals(MakeCollection.list(literal("SELECT ?x WHERE {}")), t.getElements());
	}
	
	@Test public void testSingleSimpleInsertion() {
		Template t = new Template("$Alpha");
		assertEquals(MakeCollection.list(param("Alpha")), t.getElements());
	}
	
	@Test public void testSingleTypedInsertion() {
		Template t = new Template("$Alpha:Fragment");
		assertEquals(MakeCollection.list(param("Alpha", "Fragment")), t.getElements());
	}
	
	@Test public void testMultipleParts() {
		Template t = new Template("before $Alpha then $beta end");
		List<Element> expected = MakeCollection.list
			( literal("before ")
			, param("Alpha")
			, literal(" then ")
			, param("beta")
			, literal(" end")
			);
		assertEquals(expected, t.getElements());
	}
}
