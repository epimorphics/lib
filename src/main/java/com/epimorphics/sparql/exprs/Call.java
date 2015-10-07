/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.exprs;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsExpr;

public class Call extends Common implements IsExpr {
	
	public Call(Op op, IsExpr... args) {
		super(op, args);
	}
	
	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append(op.getName());
		sb.append("(");
		String gap = "";
		for (IsExpr x: operands) {
			sb.append(gap);
			gap = ", ";
			x.toSparql(s, sb);
		}
		sb.append(")");
	}
}