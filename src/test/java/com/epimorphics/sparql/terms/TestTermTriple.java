/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestTermTriple {

	public static class TermTriple {

		final TermAtomic S, P, O;
		
		public TermTriple(TermAtomic S, TermAtomic P, TermAtomic O) {
			this.S = S;
			this.P = P;
			this.O = O;
		}

		public TermAtomic getS() {
			return S;
		}

		public TermAtomic getP() {
			return P;
		}

		public TermAtomic getO() {
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
}
