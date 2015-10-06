/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.expr;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.TermExpr;

public class ExprPrefix extends ExprBase implements TermExpr {
	
	public ExprPrefix(Op op, TermExpr... args) {
		super(op, args);
	}
	
	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append(op.getName());
		sb.append("(");
		String gap = "";
		for (TermExpr x: operands) {
			sb.append(gap);
			gap = ", ";
			x.toSparql(s, sb);
		}
		sb.append(")");
	}
}