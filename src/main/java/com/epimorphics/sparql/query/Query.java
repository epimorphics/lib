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
import com.epimorphics.sparql.terms.TermAtomic;
import com.epimorphics.sparql.terms.Triple;

public class Query {
	
	static public enum Distinction {NONE, DISTINCT, REDUCED}
	
	protected long limit = -1;
	protected long offset = -1;

	protected Distinction distinction = Distinction.NONE;
	
	final List<Projection> selectedVars = new ArrayList<Projection>();
	
	final List<OrderCondition> orderBy = new ArrayList<OrderCondition>();
	
	final List<GraphPattern> where = new ArrayList<GraphPattern>();
	
	final List<Triple> constructions = new ArrayList<Triple>();
	
	final List<TermAtomic> describeElements = new ArrayList<TermAtomic>();

	public String toSparqlSelect(Settings s) {
		StringBuilder sb = new StringBuilder();
		toSparqlSelect(s, sb);
		StringBuilder other = new StringBuilder();
		assemblePrefixes(s, other);
		other.append(sb);
		return other.toString();
	}
	
	public String toSparqlDescribe(Settings s) {
		StringBuilder sb = new StringBuilder();
		toSparqlDescribe(s, sb);
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
	
	public void toSparqlSelect(Settings s, StringBuilder sb) {
		sb.append("SELECT");
		if (distinction != Distinction.NONE) {
			sb.append(" ").append(distinction);
		}
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
	
	public void toSparqlDescribe(Settings s, StringBuilder sb) {
		sb.append("DESCRIBE");
		for (TermAtomic t: describeElements) {
			sb.append(" ");
			t.toSparql(s, sb);
		}
		if (where.size() > 0) appendWhere(s, sb);
		appendModifiers(s, sb);
	}

	private void appendWhereAndModifiers(Settings s, StringBuilder sb) {
		appendWhere(s, sb);
		appendModifiers(s, sb);
	}

	private void appendWhere(Settings s, StringBuilder sb) {
		sb.append(" WHERE ");
		whereToSparql(s, sb);
	}

	private void appendModifiers(Settings s, StringBuilder sb) {
		if (orderBy.size() > 0) {
			sb.append(" ORDER BY" );
			for (OrderCondition oc: orderBy) {
				oc.toSparql(s, sb);
			}
		}
		
		if (limit > -1) sb.append(" LIMIT ").append(limit);
		if (offset > -1) sb.append(" OFFSET ").append(offset);
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

	public void setDistinction(Distinction d) {
		this.distinction = d;
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

	public void addDescribeElements(List<TermAtomic> elements) {
		describeElements.addAll(elements);
	}
	
}