/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import static com.epimorphics.util.SparqlUtils.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.propertypaths.PropertyPath;
import com.epimorphics.sparql.propertypaths.Alt;
import com.epimorphics.sparql.propertypaths.Inv;
import com.epimorphics.sparql.propertypaths.Property;
import com.epimorphics.sparql.propertypaths.Rep;
import com.epimorphics.sparql.propertypaths.Seq;

public class TestTermTriple {

	static final URI type = new URI("http://example.com/type/T");
	
	static final URI S = new URI("http://example.com/S");
	
	static final URI P = new URI("http://example.com/properties/P");
	static final URI Q = new URI("http://example.com/properties/Q");
	static final URI R = new URI("http://example.com/properties/R");

	static final Literal O = new Literal("chat", type, "");

	static final Triple SPO = new Triple(S, P, O);

	@Test public void testTripleConstruction() {
		
		assertSame(S, SPO.getS());
		assertSame(P, SPO.getP());
		assertSame(O, SPO.getO());
		
		assertEquals(SPO, new Triple(S, P, O));
	}
	
	@Test public void testTripleToSparql() {
		String result = renderToSparql(SPO);
		assertEquals("<http://example.com/S> <http://example.com/properties/P> 'chat'^^<http://example.com/type/T> .", result);
	}
	
	@Test public void testPrimitive() {
		URI P = new URI("http://example.com/properties/P");
		PropertyPath p = new Property(P);
		assertEquals("<" + P.getURI() + ">", renderToSparql(p));
	}
	
	@Test public void testRepetition() {
		PropertyPath p = new Property(P);
		assertEquals("<" + P.getURI() + ">+", renderToSparql(new Rep(p, PropertyPath.Repeat.ONEMORE)));
		assertEquals("<" + P.getURI() + ">*", renderToSparql(new Rep(p, PropertyPath.Repeat.ZEROMORE)));
		assertEquals("<" + P.getURI() + ">?", renderToSparql(new Rep(p, PropertyPath.Repeat.OPTIONAL)));
	}
	
	@Test public void testInverse() {
		PropertyPath p = new Property(P);
		PropertyPath q = new Inv(p);
		assertEquals("^<" + P.getURI() + ">", renderToSparql(q));
	}
	
	@Test public void testSeq() {
		PropertyPath pq = new Seq(new Property(P), new Property(Q));
		String expected = "<" + P.getURI() + ">/<" + Q.getURI() + ">";
		assertEquals(expected, renderToSparql(pq));
	}
	
	@Test public void testAlt() {
		PropertyPath pq = new Alt(new Property(P), new Property(Q));
		String expected = "<" + P.getURI() + ">|<" + Q.getURI() + ">";
		assertEquals(expected, renderToSparql(pq));
	}
	
	@Test public void testPrecedenceEnvOfAlt() {
		String pp = "<" + P.getURI() + ">", pq = "<" + Q.getURI() + ">";
		PropertyPath alt = new Alt(new Property(P), new Property(Q));
		PropertyPath invAlt = new Inv(alt);
		assertEquals("^(" + pp + "|" + pq + ")", renderToSparql(invAlt));
	}
	
	@Test public void testPrecedenceEnvOfSeq() {
		String pp = "<" + P.getURI() + ">", pq = "<" + Q.getURI() + ">";
		PropertyPath seq = new Seq(new Property(P), new Property(Q));
		PropertyPath invSeq = new Inv(seq);
		assertEquals("^(" + pp + "/" + pq + ")", renderToSparql(invSeq));
	}
	
	@Test public void testPrecedenceRepOfAlt() {
		String pp = "<" + P.getURI() + ">", pq = "<" + Q.getURI() + ">";
		PropertyPath alt = new Alt(new Property(P), new Property(Q));
		PropertyPath repAlt = new Rep(alt, PropertyPath.Repeat.OPTIONAL);
		assertEquals("(" + pp + "|" + pq + ")?", renderToSparql(repAlt));
	}
	
	@Test public void testPrecedenceRepOfSeq() {
		String pp = "<" + P.getURI() + ">", pq = "<" + Q.getURI() + ">";
		PropertyPath seq = new Seq(new Property(P), new Property(Q));
		PropertyPath repSeq = new Rep(seq, PropertyPath.Repeat.OPTIONAL);
		assertEquals("(" + pp + "/" + pq + ")?", renderToSparql(repSeq));
	}
	
	@Test public void testPrecedenceSeqOfAlt() {
		String pp = "<" + P.getURI() + ">";
		String pq = "<" + Q.getURI() + ">";
		String pr = "<" + R.getURI() + ">";
		String ps = "<" + S.getURI() + ">";
		
		PropertyPath altA = new Alt(new Property(P), new Property(Q));
		PropertyPath altB = new Alt(new Property(R), new Property(S));
		PropertyPath seq = new Seq(altA, altB);
		String altAres = "(" + pp + "|" + pq + ")"; 
		String altBres = "(" + pr + "|" + ps + ")";
		assertEquals(altAres + "/" + altBres, renderToSparql(seq));
	}
	
}
