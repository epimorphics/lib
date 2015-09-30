/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.templates.Settings;

public class TestTermTriple {

	@Test public void testTripleConstruction() {
		TermURI type = new TermURI("http://example.com/type/T");
		TermURI S = new TermURI("http://example.com/S");
		TermURI P = new TermURI("http://example.com/P");
		TermLiteral O = new TermLiteral("chat", type, "");
		TermTriple t1 = new TermTriple(S, P, O);
		
		assertSame(S, t1.getS());
		assertSame(P, t1.getP());
		assertSame(O, t1.getO());
		
		assertEquals(t1, new TermTriple(S, P, O));
	}
	
	@Test public void testTripleToSparql() {
		TermURI type = new TermURI("http://example.com/type/T");
		TermURI S = new TermURI("http://example.com/S");
		TermURI P = new TermURI("http://example.com/P");
		TermLiteral O = new TermLiteral("chat", type, "");
		TermTriple t1 = new TermTriple(S, P, O);
		
		StringBuilder sb = new StringBuilder();
		Settings s = new Settings();
		t1.toSparql(s, sb);
		String result = sb.toString();
		
		assertEquals("<http://example.com/S> <http://example.com/P> 'chat'^^<http://example.com/type/T> .", result);
	}
}
