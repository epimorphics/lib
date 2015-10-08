/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.exprs;

import java.util.Arrays;
import java.util.List;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsExpr;

public abstract class Common implements IsExpr {

	final Op op;
	final List<IsExpr> operands;

	public Common(Op op, IsExpr... args) {
		this.op = op;
		this.operands = Arrays.asList(args);
	}
	
	public Common(Op op, List<IsExpr> args) {
		this.op = op;
		this.operands = args;
	}
	
	public Op getOp() {
		return op;
	}
	
	public List<IsExpr> getOperands() {
		return operands;
	}
	
	@Override public void toSparql(int precedence, Settings s, StringBuilder sb) {
		toSparql(s, sb);
	}
}
