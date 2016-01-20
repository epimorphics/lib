/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.text;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.graphpatterns.GraphPattern;
import com.epimorphics.sparql.query.QueryShape;
import com.epimorphics.sparql.terms.Literal;
import com.epimorphics.sparql.terms.TermList;
import com.epimorphics.sparql.terms.Triple;
import com.epimorphics.sparql.terms.URI;
import com.epimorphics.sparql.terms.Var;

public class TestTextTransform {
	
	TextTransformerByJenaText J = new TextTransformerByJenaText();
	
	@Test public void testTransformByJenaText() {
		
		Var V = new Var("v");
		URI P = new URI("eh:/P");
		QueryShape q = new QueryShape();
		TextQuery tq = new TextQuery(V, P, "target");
		q.setTextQuery(tq);
		
		QueryShape newq = J.apply(q);
		
		List<GraphPattern> obtained = newq.getEarly();
		
		List<GraphPattern> expecteds = new ArrayList<GraphPattern>();

		Literal target = new Literal(tq.target, "");
		Literal BIG = Literal.fromNumber(1000000000);
		TermList args = new TermList(tq.property, target, BIG);
		URI queryProperty = new URI("http://jena.apache.org/text#query");

		Triple expected = new Triple(tq.var, queryProperty, args);
		expecteds.add(new Basic(expected));
		
		assertEquals(expecteds, obtained);		
		
	}
}
