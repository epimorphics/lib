/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.expr;

import java.util.Arrays;
import java.util.List;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.Op;
import com.epimorphics.sparql.terms.TermExpr;

public class ExprPrefix implements TermExpr {

	final Op op;
	final List<TermExpr> operands;
	
	public ExprPrefix(Op op, TermExpr... args) {
		this.op = op;
		this.operands = Arrays.asList(args);
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
	
	public Op getOp() {
		return op;
	}
	public List<TermExpr> getOperands() {
		return operands;
	}
	
}