/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.propertypaths.PropertyPath;
import com.epimorphics.sparql.propertypaths.PropertyPathAlt;
import com.epimorphics.sparql.propertypaths.PropertyPathInv;
import com.epimorphics.sparql.propertypaths.PropertyPathProperty;
import com.epimorphics.sparql.propertypaths.PropertyPathRep;
import com.epimorphics.sparql.propertypaths.PropertyPathSeq;

import static com.epimorphics.test.utils.SparqlUtils.*;

public class TestTermTriple {

	static final TermURI type = new TermURI("http://example.com/type/T");
	
	static final TermURI S = new TermURI("http://example.com/S");
	
	static final TermURI P = new TermURI("http://example.com/properties/P");
	static final TermURI Q = new TermURI("http://example.com/properties/Q");

	static final TermLiteral O = new TermLiteral("chat", type, "");

	static final TermTriple SPO = new TermTriple(S, P, O);

	@Test public void testTripleConstruction() {
		
		assertSame(S, SPO.getS());
		assertSame(P, SPO.getP());
		assertSame(O, SPO.getO());
		
		assertEquals(SPO, new TermTriple(S, P, O));
	}
	
	@Test public void testTripleToSparql() {
		String result = renderToSparql(SPO);
		assertEquals("<http://example.com/S> <http://example.com/properties/P> 'chat'^^<http://example.com/type/T> .", result);
	}
	
	@Test public void testPrimitive() {
		TermURI P = new TermURI("http://example.com/properties/P");
		PropertyPath p = new PropertyPathProperty(P);
		assertEquals("<" + P.getURI() + ">", renderToSparql(p));
	}
	
	@Test public void testRepetition() {
		PropertyPath p = new PropertyPathProperty(P);
		assertEquals("<" + P.getURI() + ">+", renderToSparql(new PropertyPathRep(p, PropertyPath.Repeat.ONEMORE)));
		assertEquals("<" + P.getURI() + ">*", renderToSparql(new PropertyPathRep(p, PropertyPath.Repeat.ZEROMORE)));
		assertEquals("<" + P.getURI() + ">?", renderToSparql(new PropertyPathRep(p, PropertyPath.Repeat.OPTIONAL)));
	}
	
	@Test public void testInverse() {
		PropertyPath p = new PropertyPathProperty(P);
		PropertyPath q = new PropertyPathInv(p);
		assertEquals("^<" + P.getURI() + ">", renderToSparql(q));
	}
	
	@Test public void testSeq() {
		PropertyPath pq = new PropertyPathSeq(new PropertyPathProperty(P), new PropertyPathProperty(Q));
		String expected = "<" + P.getURI() + ">/<" + Q.getURI() + ">";
		assertEquals(expected, renderToSparql(pq));
	}
	
	@Test public void testAlt() {
		PropertyPath pq = new PropertyPathAlt(new PropertyPathProperty(P), new PropertyPathProperty(Q));
		String expected = "<" + P.getURI() + ">|<" + Q.getURI() + ">";
		assertEquals(expected, renderToSparql(pq));
	}
	
	@Test public void testPrecedenceEnvOfAlt() {
		String pp = "<" + P.getURI() + ">", pq = "<" + Q.getURI() + ">";
		PropertyPath alt = new PropertyPathAlt(new PropertyPathProperty(P), new PropertyPathProperty(Q));
		PropertyPath invAlt = new PropertyPathInv(alt);
		assertEquals("^(" + pp + "|" + pq + ")", renderToSparql(invAlt));
	}
	
	@Test public void testPrecedenceRepOfAlt() {
		String pp = "<" + P.getURI() + ">", pq = "<" + Q.getURI() + ">";
		PropertyPath alt = new PropertyPathAlt(new PropertyPathProperty(P), new PropertyPathProperty(Q));
		PropertyPath repAlt = new PropertyPathRep(alt, PropertyPath.Repeat.OPTIONAL);
		assertEquals("(" + pp + "|" + pq + ")?", renderToSparql(repAlt));
	}
	
}
