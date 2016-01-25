/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.text;

import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.query.QueryShape;
import com.epimorphics.sparql.query.Transform;
import com.epimorphics.sparql.terms.Literal;
import com.epimorphics.sparql.terms.TermList;
import com.epimorphics.sparql.terms.Triple;
import com.epimorphics.sparql.terms.URI;
import com.epimorphics.sparql.terms.Var;

public class TextTransformerByJenaText implements Transform {

	static final Literal BIG = Literal.fromNumber(1000000000);

	protected Var var;
	
	protected URI queryProperty = new URI("http://jena.apache.org/text#query");
	
	protected URI searchProperty = null;
	
	@Override public QueryShape apply(QueryShape q) {
		TextQuery gq = q.getTextQuery();
		if (gq == null) return q;
		return asJenaText(q, gq);
	}
	
	public void setVar(String name) {
		var = new Var(name);
	}
	
	public void setQueryProperty(String u) {
		queryProperty = new URI(u);
	}
	
	public void setSearchProperty(String u) {
		searchProperty = new URI(u);
	}

	private QueryShape asJenaText(QueryShape q, TextQuery gq) {
		QueryShape newq = q.copy();
		Var v = (var == null ? gq.var : var);
		Literal target = new Literal(gq.target, "");
		URI search = (searchProperty == null ? gq.property : searchProperty);
		TermList args = search == null
			? new TermList(target, BIG)
			: new TermList(search, target, BIG)
			;
		Triple t = new Triple(v, queryProperty, args );
		newq.addEarlyPattern(new Basic(t));
		return newq;
	}

	@Override public String getFullName() {
		return "TextQuery:ByJenaText";
	}
	
}