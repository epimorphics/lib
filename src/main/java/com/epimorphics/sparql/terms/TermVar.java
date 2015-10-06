/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import com.epimorphics.sparql.templates.Settings;

public class TermVar extends Spelling implements TermExpr, TermAtomic, TermSparql, TermProjection {
	
	public TermVar(String spelling) {
		super(spelling);
	}

	public String getName() {
		return spelling;
	}

	public String toString() {
		return "?" + spelling;
	}
	
	public boolean equals(Object other) {
		return other instanceof TermVar && same((TermVar) other);
	}

	private boolean same(TermVar other) {
		return spelling.equals(other.spelling);
	}
	
	public int hashCode() {
		return spelling.hashCode();
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append("?").append(spelling);
	}
	
	@Override public void toSparql(int precedence, Settings s, StringBuilder sb) {
		toSparql(s, sb);
	}
}
