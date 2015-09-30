/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.templates.Settings;

public class TestTermTriple {

	public static class TermTriple implements TermSparql {

		final TermSparql S, P, O;
		
		public TermTriple(TermSparql S, TermSparql P, TermSparql O) {
			this.S = S;
			this.P = P;
			this.O = O;
		}

		public TermSparql getS() {
			return S;
		}

		public TermSparql getP() {
			return P;
		}

		public TermSparql getO() {
			return O;
		}
		
		public String toString() {
			return "[" + S + ", " + P + ", " + O + "]";
		}
		
		public boolean equals(Object other) {
			return other instanceof TermTriple && same((TermTriple) other);
		}

		private boolean same(TermTriple other) {
			return S.equals(other.S) && P.equals(other.P) && O.equals(other.O);
		}
		
		public int hashCode() {
			return S.hashCode() ^ P.hashCode() + O.hashCode();
		}

		@Override public void toSparql(Settings s, StringBuilder sb) {
			S.toSparql(s, sb);
			sb.append(" ");
			P.toSparql(s, sb);
			sb.append(" ");
			O.toSparql(s, sb);
			sb.append(" .");
		}
		
	}
	
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
		
		assertEquals("<http://example.com/S> <http://example.com/P> 'chat'^^<http://example.com/type/T>", result);
	}
}
