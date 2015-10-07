/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import java.util.ArrayList;
import java.util.List;

import com.epimorphics.sparql.exprs.Infix;
import com.epimorphics.sparql.graphpatterns.GraphPattern;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsExpr;
import com.epimorphics.sparql.terms.Projection;
import com.epimorphics.sparql.terms.IsSparqler;

public class Query {
	
	public static enum Order {DESC, ASC}
	
	public static class OrderCondition implements IsSparqler {
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
	
	protected GraphPattern where = new GraphPattern() {
		
		@Override public void toSparql(Settings s, StringBuilder sb) {
			sb.append("{}");
		}
	};
	
	protected int limit = -1;
	protected int offset = -1;
	
	final List<Projection> selectedVars = new ArrayList<Projection>();
	
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
			for (Projection x: selectedVars) {
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

	public void addProjection(Projection x) {
		selectedVars.add(x);
	}

	public void addOrder(Order o, IsExpr e) {
		orderBy.add(new OrderCondition(o, e));
	}
	
}