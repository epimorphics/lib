/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsExpr;
import com.epimorphics.sparql.terms.IsSparqler;

public class OrderCondition implements IsSparqler {
	final Order order;
	final IsExpr expr;
	
	public OrderCondition(Order order, IsExpr expr) {
		this.order = order;
		this.expr = expr;
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append(" ");
		sb.append(order);
		sb.append("(");
		expr.toSparql(s, sb);
		sb.append(")");
	}
}