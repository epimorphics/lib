/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.expr;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.TermExpr;

public class ExprInfix extends ExprBase implements TermExpr {
	
	public ExprInfix(TermExpr L, Op op, TermExpr R) {
		super(op, L, R);
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		getL().toSparql(s, sb);
		sb.append(" ").append(op.getName()).append(" ");
		getR().toSparql(s, sb);
	}

	public TermExpr getL() {
		return operands.get(0);
	}

	public TermExpr getR() {
		return operands.get(1);
	}
}