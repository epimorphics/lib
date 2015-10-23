/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.epimorphics.sparql.graphpatterns.GraphPattern;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsExpr;
import com.epimorphics.sparql.terms.Projection;
import com.epimorphics.sparql.terms.Triple;

public class Query {
	
	protected long limit = -1;
	protected long offset = -1;
	
	final List<Projection> selectedVars = new ArrayList<Projection>();
	
	final List<OrderCondition> orderBy = new ArrayList<OrderCondition>();
	
	final List<GraphPattern> where = new ArrayList<GraphPattern>();
	
	final List<Triple> constructions = new ArrayList<Triple>();

	public String toSparqlSelect(Settings s) {
		StringBuilder sb = new StringBuilder();
		toSparqlSelect(s, sb);
		StringBuilder other = new StringBuilder();
		assemblePrefixes(s, other);
		other.append(sb);
		return other.toString();
	}
	
	public String toSparqlConstruct(Settings s) {
		StringBuilder sb = new StringBuilder();
		toSparqlConstruct(s, sb);
		StringBuilder other = new StringBuilder();
		assemblePrefixes(s, other);
		other.append(sb);
		return other.toString();
	}

	private void assemblePrefixes(Settings s, StringBuilder sb) {
		Map<String, String> prefixes = s.getPrefixes();
		for (String prefix: s.getUsedPrefixes()) {
			String uri = prefixes.get(prefix);
			sb.append("PREFIX ").append(prefix).append(": <").append(uri).append(">\n");
		}
	}

	private void toSparqlConstruct(Settings s, StringBuilder sb) {
		sb.append("CONSTRUCT {");
		String before = "";
		for (Triple t: constructions) {
			sb.append(before);
			t.toSparql(s, sb);
			before = " ";
		}
		sb.append("}");
		appendWhereAndModifiers(s, sb);
	}
	
	private void toSparqlSelect(Settings s, StringBuilder sb) {
		sb.append("SELECT");
		if (selectedVars.isEmpty()) {
			sb.append(" *");
		} else {
			for (Projection x: selectedVars) {
				sb.append(" ");
				x.toSparql(s, sb);
			}
		}
		appendWhereAndModifiers(s, sb);
	}

	private void appendWhereAndModifiers(Settings s, StringBuilder sb) {
		sb.append(" WHERE ");
		whereToSparql(s, sb);
		
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
	
	protected void whereToSparql(Settings s, StringBuilder sb) {
		if (where.size() == 1) {
			where.get(0).toSparql(s, sb);
		} else {
			sb.append("{");
			for (GraphPattern element: where) element.toSparql(s, sb);
			sb.append("}");
		}
	}

	public void setPattern(GraphPattern where) {
		this.where.clear();
		addPattern(where);
	}
	
	public void addPattern(GraphPattern wherePart) {
		where.add(wherePart);
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public void addProjection(Projection x) {
		selectedVars.add(x);
	}

	public void addOrder(Order o, IsExpr e) {
		orderBy.add(new OrderCondition(o, e));
	}

	public void construct(Triple t) {
		constructions.add(t);
	}
	
}