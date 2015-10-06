/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import com.epimorphics.sparql.patterns.PatternBase;
import com.epimorphics.sparql.templates.Settings;

public class TermTriple implements TermSparql, PatternBase {

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

	@Override public void toSparql(Settings s, StringBuilder sb) {
		S.toSparql(s, sb);
		sb.append(" ");
		P.toSparql(s, sb);
		sb.append(" ");
		O.toSparql(s, sb);
		sb.append(" .");
	}
	
}