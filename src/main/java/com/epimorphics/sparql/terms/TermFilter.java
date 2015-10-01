/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import com.epimorphics.sparql.templates.Settings;

public class TermFilter implements TermSparql {

	final TermExpr e;
	
	public TermFilter(TermExpr e) {
		this.e = e;
	}
	
	public String toString() {
		return "FILTER(" + e.toString() + ")";
	}

	public TermExpr getExpr() {
		return e;
	}
	
	public boolean equals(Object other) {
		return other instanceof TermFilter && same((TermFilter) other);
	}

	private boolean same(TermFilter other) {
		return e.equals(other.e);
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append("FILTER(");
		e.toSparql(s, sb);
		sb.append(")");
	}
}