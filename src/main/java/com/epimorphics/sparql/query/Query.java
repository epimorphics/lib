/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import java.util.ArrayList;
import java.util.List;

import com.epimorphics.sparql.expr.ExprInfix;
import com.epimorphics.sparql.patterns.GraphPattern;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.TermExpr;
import com.epimorphics.sparql.terms.TermProjection;
import com.epimorphics.sparql.terms.TermSparql;

public class Query {
	
	public static enum Order {DESC, ASC}
	
	public static class OrderCondition implements TermSparql {
		final Order order;
		final TermExpr expr;
		
		public OrderCondition(Order order, TermExpr expr) {
			this.order = order;
			this.expr = expr;
		}

		@Override public void toSparql(Settings s, StringBuilder sb) {
			sb.append(" ");
			sb.append(order);
			sb.append(" ");
			if (expr instanceof ExprInfix) sb.append("(");
			expr.toSparql(s, sb);
			if (expr instanceof ExprInfix) sb.append(")");
		}
	}
	
	protected GraphPattern where = new GraphPattern() {
		
		@Override public void toSparql(Settings s, StringBuilder sb) {
			sb.append("{}");
		}
	};
	
	protected int limit = -1;
	protected int offset = -1;
	
	final List<TermProjection> selectedVars = new ArrayList<TermProjection>();
	
	final List<OrderCondition> orderBy = new ArrayList<OrderCondition>();

	public String toSparql(Settings s) {
		StringBuilder sb = new StringBuilder();
		toSparql(s, sb);
		return sb.toString();
	}

	private void toSparql(Settings s, StringBuilder sb) {
		sb.append("SELECT");
		if (selectedVars.isEmpty()) {
			sb.append(" *");
		} else {
			for (TermProjection x: selectedVars) {
				sb.append(" ");
				x.toSparql(s, sb);
			}
		}
		sb.append(" WHERE ");
		where.toSparql(s, sb);
		
		if (orderBy.size() > 0) {
			sb.append(" ORDER BY" );
			for (OrderCondition oc: orderBy) {
				oc.toSparql(s, sb);
			}
		}
		
		if (limit > -1) sb.append(" LIMIT ").append(limit);
		if (offset > -1) sb.append(" OFFSET ").append(offset);
		sb.append("");
	}

	public void setPattern(GraphPattern where) {
		this.where = where;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void addProjection(TermProjection x) {
		selectedVars.add(x);
	}

	public void addOrder(Order o, TermExpr e) {
		orderBy.add(new OrderCondition(o, e));
	}
	
}