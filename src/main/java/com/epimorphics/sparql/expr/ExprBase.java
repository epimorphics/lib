/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.expr;

import java.util.Arrays;
import java.util.List;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.TermExpr;

public abstract class ExprBase implements TermExpr {

	final Op op;
	final List<TermExpr> operands;

	public ExprBase(Op op, TermExpr... args) {
		this.op = op;
		this.operands = Arrays.asList(args);
	}
	
	public Op getOp() {
		return op;
	}
	
	public List<TermExpr> getOperands() {
		return operands;
	}
	
	@Override public void toSparql(int precedence, Settings s, StringBuilder sb) {
		toSparql(s, sb);
	}
}
