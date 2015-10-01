/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.expr;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.Op;
import com.epimorphics.sparql.terms.TermExpr;

public class ExprInfix implements TermExpr {

	final TermExpr L, R;
	final Op op;
	
	public ExprInfix(TermExpr L, Op op, TermExpr R) {
		this.L = L;
		this.op = op;
		this.R = R;
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		L.toSparql(s, sb);
		sb.append(" ").append(op.getName()).append(" ");
		R.toSparql(s, sb);
	}

	public TermExpr getL() {
		return L;
	}

	public Op getOp() {
		return op;
	}

	public TermExpr getR() {
		return null;
	}
}