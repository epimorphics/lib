/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import com.epimorphics.sparql.graphpatterns.PatternCommon;
import com.epimorphics.sparql.templates.Settings;

public class Filter implements PatternCommon, IsSparqler, TripleOrFilter {

	final IsExpr e;
	
	public Filter(IsExpr e) {
		this.e = e;
	}
	
	public String toString() {
		return "FILTER(" + e.toString() + ")";
	}

	public IsExpr getExpr() {
		return e;
	}
	
	public boolean equals(Object other) {
		return other instanceof Filter && same((Filter) other);
	}

	private boolean same(Filter other) {
		return e.equals(other.e);
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append("FILTER(");
		e.toSparql(s, sb);
		sb.append(")");
	}
}