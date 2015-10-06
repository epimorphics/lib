/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.test.utils.SparqlUtils;

public class TestTermTriple {

	static final TermURI type = new TermURI("http://example.com/type/T");
	static final TermURI S = new TermURI("http://example.com/S");
	static final TermURI P = new TermURI("http://example.com/P");

	static final TermLiteral O = new TermLiteral("chat", type, "");

	static final TermTriple SPO = new TermTriple(S, P, O);

	@Test public void testTripleConstruction() {
		
		assertSame(S, SPO.getS());
		assertSame(P, SPO.getP());
		assertSame(O, SPO.getO());
		
		assertEquals(SPO, new TermTriple(S, P, O));
	}
	
	@Test public void testTripleToSparql() {
		String result = SparqlUtils.renderToSparql(SPO);
		assertEquals("<http://example.com/S> <http://example.com/P> 'chat'^^<http://example.com/type/T> .", result);
	}
	
	@Test public void testPropertyPathsToSparql() {
		
	}
	
}
