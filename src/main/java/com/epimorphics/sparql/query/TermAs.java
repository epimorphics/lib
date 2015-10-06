/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/

package com.epimorphics.sparql.query;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.TermExpr;
import com.epimorphics.sparql.terms.TermProjection;
import com.epimorphics.sparql.terms.TermVar;

public class TermAs implements TermProjection {

	final TermExpr e;
	final TermVar v;
	
	public TermAs(TermExpr e, TermVar v) {
		this.e = e;
		this.v = v;
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append("(");
		e.toSparql(s, sb);
		sb.append(" AS ");
		v.toSparql(s, sb);
		sb.append(")");
	}
	
}
