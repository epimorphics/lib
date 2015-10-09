/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.exprs;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsExpr;

public class Infix extends ExprCommon implements IsExpr {
	
	public Infix(IsExpr L, Op op, IsExpr R) {
		super(op, L, R);
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		getL().toSparql(op.precedence, s, sb);
		sb.append(" ").append(op.getName()).append(" ");
		getR().toSparql(op.precedence, s, sb);
	}
	
	@Override public void toSparql(int precedence, Settings s, StringBuilder sb) {
		if (op.precedence > precedence) sb.append("(");
		toSparql(s, sb);
		if (op.precedence > precedence) sb.append(")");
	}

	public IsExpr getL() {
		return operands.get(0);
	}

	public IsExpr getR() {
		return operands.get(1);
	}
}