/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.text;

import com.epimorphics.sparql.exprs.Call;
import com.epimorphics.sparql.exprs.Op;
import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.query.QueryShape;
import com.epimorphics.sparql.query.Transform;
import com.epimorphics.sparql.terms.Filter;
import com.epimorphics.sparql.terms.Literal;
import com.epimorphics.sparql.terms.Triple;
import com.epimorphics.sparql.terms.Var;

public class TextTransformerBySparql implements Transform {

	@Override public QueryShape apply(QueryShape q) {
		TextQuery tq = q.getTextQuery();
		return (tq == null ? q : asSparql(q, tq));
	}
	
	@Override public String getFullName() {
		return "TextQuery:BySparql";
	}

	private QueryShape asSparql(QueryShape q, TextQuery tq) {
		Var value = new Var(tq.var.getName() + "_text");
		Literal i = new Literal("i", "");
		QueryShape newq = q.copy();
		Literal pattern = new Literal(asPattern(tq.target), "");
		Triple t = new Triple(tq.var, tq.property, value);
		Filter f = new Filter(new Call(Op.fnREGEX, value, pattern, i));
		newq.addEarlyPattern(new Basic(t, f));
		return newq;
	}

	private String asPattern(String target) {
		return "\\\\b" + target.replaceAll("[^A-Za-z0-9]+", "") + "\\\\b";
	}

}
