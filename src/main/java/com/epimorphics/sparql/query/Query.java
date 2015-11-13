/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.RDFNode;

import com.epimorphics.sparql.graphpatterns.Bind;
import com.epimorphics.sparql.graphpatterns.GraphPattern;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.templates.Template;
import com.epimorphics.sparql.terms.IsExpr;
import com.epimorphics.sparql.terms.Literal;
import com.epimorphics.sparql.terms.Projection;
import com.epimorphics.sparql.terms.TermAtomic;
import com.epimorphics.sparql.terms.Triple;
import com.epimorphics.sparql.terms.URI;
import com.epimorphics.util.SparqlUtils;

/**
	A Query is a representation of a SPARQL query.

*/
public class Query {
	
	static public enum Distinction {NONE, DISTINCT, REDUCED}
	
	protected long limit = -1;
	protected long offset = -1;

	protected Distinction distinction = Distinction.NONE;
	
	protected Template template = null;
	
	final List<Projection> selectedVars = new ArrayList<Projection>();
	
	final List<OrderCondition> orderBy = new ArrayList<OrderCondition>();
	
	final List<GraphPattern> earlyWhere = new ArrayList<GraphPattern>();
	
	final List<GraphPattern> laterWhere = new ArrayList<GraphPattern>();
	
	final List<Triple> constructions = new ArrayList<Triple>();
	
	final List<TermAtomic> describeElements = new ArrayList<TermAtomic>();
	
	final List<String> rawModifiers = new ArrayList<String>();

	/**
		copy() returns a copy of this query. The array-valued instance
		variables are themselves copied.
	*/
	public Query copy() {
		Query q = new Query();
		q.limit = limit;
		q.offset = offset;
		q.distinction = distinction;
		q.template = template;
		q.selectedVars.addAll(selectedVars);
		q.orderBy.addAll(orderBy);
		q.rawModifiers.addAll(rawModifiers);
		q.earlyWhere.addAll(earlyWhere);
		q.laterWhere.addAll(laterWhere);
		q.constructions.addAll(constructions);
		q.describeElements.addAll(describeElements);
		return q;
	}
	
	public String toSparqlSelect(Settings s) {
		if (template != null) return templateToSparql("SELECT ", s);
		StringBuilder sb = new StringBuilder();
		toSparqlSelect(s, sb);
		StringBuilder other = new StringBuilder();
		assemblePrefixes(s, other);
		other.append(sb);
		return other.toString();
	}

	public String toSparqlDescribe(Settings s) {
		if (template != null) return templateToSparql("DESCRIBE ", s);
		StringBuilder sb = new StringBuilder();
		toSparqlDescribe(s, sb);
		StringBuilder other = new StringBuilder();
		assemblePrefixes(s, other);
		other.append(sb);
		return other.toString();
	}

	public String toSparqlConstruct(Settings s) {
		if (template != null) return templateToSparql("CONSTRUCT ", s);
		StringBuilder sb = new StringBuilder();
		toSparqlConstruct(s, sb);
		StringBuilder other = new StringBuilder();
		assemblePrefixes(s, other);
		other.append(sb);
		return other.toString();
	}
	
	private String templateToSparql(String queryType, Settings s) {
		if (!template.startsWith(queryType)) 
			throw new IllegalArgumentException("template does not start with " + queryType + " but " + template.toString());
		fillParams(s);
		return doBinding(template.substWith(s));
	}	

	private String doBinding(String target) {
		for (GraphPattern e: earlyWhere) 
			if (e instanceof Bind)
				target = doBinding(target, (Bind) e);			
		return target;
	}

	private String doBinding(String target, Bind e) {
		return target.replace
			( "?" + e.getVar().getName()
			, SparqlUtils.renderToSparql(e.getExpr())
			);
	}
    
	private void fillParams(Settings s) {
		s.putParam("_graphPattern", new SubstPattern(this));
		s.putParam("_sort", new SubstSort(this));
		s.putParam("_modifiers", new SubstMod(this));
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
		if (describeElements.isEmpty()) {
			sb.append(" ?id");
		} else {
			for (TermAtomic t: describeElements) {
				sb.append(" ");
				t.toSparql(s, sb);
			}
		}
		if (earlyWhere.size() > 0 || laterWhere.size() > 0) appendWhere(s, sb);
		appendOrderAndModifiers(s, sb);
	}

	private void appendWhereAndModifiers(Settings s, StringBuilder sb) {
		appendWhere(s, sb);
		appendOrderAndModifiers(s, sb);
	}

	private void appendWhere(Settings s, StringBuilder sb) {
		sb.append(" WHERE ");
		whereToSparql(s, sb);
	}

	private void appendOrderAndModifiers(Settings s, StringBuilder sb) {
		if (orderBy.size() > 0 || rawModifiers.size() > 0) {
			if (rawModifiers.isEmpty()) sb.append(" ORDER BY" );
			for (String r: rawModifiers) sb.append(r);
			for (OrderCondition oc: orderBy) {
				oc.toSparql(s, sb);
			}
		}
		appendLimitAndOffset(s, sb);
	}

	void appendLimitAndOffset(Settings s, StringBuilder sb) {
		if (limit > -1) sb.append(" LIMIT ").append(limit);
		if (offset > -1) sb.append(" OFFSET ").append(offset);
	}
	
	protected void whereToSparql(Settings s, StringBuilder sb) {
//		List<GraphPattern> all = new ArrayList<GraphPattern>();
//		all.addAll(earlyWhere);
//		all.addAll(laterWhere);
//		whereToSparql(s, sb, all);
		List<GraphPattern> early = new ArrayList<GraphPattern>();
		List<GraphPattern> later = new ArrayList<GraphPattern>();
		split(earlyWhere, early, later);
		split(laterWhere, early, later);
		early.addAll(later);
		whereToSparql(s, sb, early);
	}

	private void split(List<GraphPattern> from, List<GraphPattern> early, List<GraphPattern> later) {
		for (GraphPattern f: from) 
			if (f instanceof Bind) early.add(f); else later.add(f);
	}

	protected void whereToSparql(Settings s, StringBuilder sb, List<GraphPattern> patterns) {
//		if (patterns.size() == 1) {
//			patterns.get(0).toSparql(s, sb);
//		} else {
			sb.append("{");
			for (GraphPattern element: patterns) element.toSparql(s, sb);
			sb.append("}");
//		}
	}

	public Query setTemplate(String templateString) {
		return setTemplate(new Template(templateString));
	}

	public Query setTemplate(Template t) {
		this.template = t;
		return this;
	}
	
	public void setDistinction(Distinction d) {
		this.distinction = d;
	}
	
	public void setEarlyPattern(GraphPattern where) {
		this.earlyWhere.clear();
		addEarlyPattern(where);
	}
	
	public Query addEarlyPattern(GraphPattern p) {
		earlyWhere.add(p);
		return this;
	}
	
	public void setLaterPattern(GraphPattern where) {
		this.laterWhere.clear();
		addLaterPattern(where);
	}
	
	public Query addLaterPattern(GraphPattern p) {
		earlyWhere.add(p);
		return this;
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

	public Query addOrder(Order o, IsExpr e) {
		orderBy.add(new OrderCondition(o, e));
		return this;
	}
	
	public Query addRawModifier(String text) {
		rawModifiers.add(text);
		return this;
	}

	public void construct(Triple t) {
		constructions.add(t);
	}

	public void addDescribeElements(List<TermAtomic> elements) {
		describeElements.addAll(elements);
	}
	
}