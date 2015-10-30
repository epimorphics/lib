/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/

package com.epimorphics.sparql.graphpatterns;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsExpr;
import com.epimorphics.sparql.terms.Var;

public class Bind implements PatternCommon {

	final Var x;
	final IsExpr e;
	
	public Bind(IsExpr e, Var x) {
		this.e = e;
		this.x = x;
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append("BIND(");
		e.toSparql(s, sb);
		sb.append(" AS ");
		x.toSparql(s, sb);
		sb.append(")");
	}

	public Var getVar() {
		return x;
	}

	public IsExpr getExpr() {
		return e;
	}
}