/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/

package com.epimorphics.sparql.query;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsExpr;
import com.epimorphics.sparql.terms.Projection;
import com.epimorphics.sparql.terms.Var;

public class As implements Projection {

	final IsExpr e;
	final Var v;
	
	public As(IsExpr e, Var v) {
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
