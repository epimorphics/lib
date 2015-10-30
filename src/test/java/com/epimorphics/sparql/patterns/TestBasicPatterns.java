/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.graphpatterns.Builder;
import com.epimorphics.sparql.graphpatterns.PatternCommon;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.Filter;
import com.epimorphics.sparql.terms.Literal;
import com.epimorphics.sparql.terms.Triple;

import static com.epimorphics.util.SparqlUtils.*;
import static com.epimorphics.test.utils.MakeCollection.*;

public class TestBasicPatterns extends SharedFixtures {
	
	@Test public void testBasicTriplesPattern() {
		Builder b = new Builder();
		Triple SPA = new Triple(S, P, A);
		Triple SQB = new Triple(S, Q, B);

		b.addElement(SPA);
		b.addElement(SQB);
		Basic gp = b.build();
		
		assertEquals(list(SPA, SQB), gp.getElements());
	}
	
	@Test public void testBasicTriplePatternToSparql() {
		Triple SPA = new Triple(S, P, A);
		Filter f = new Filter(new Literal("17", Literal.xsdInteger, ""));
		List<PatternCommon> elements = new ArrayList<PatternCommon>();
		elements.add(f);
		elements.add(SPA);
		Basic b = new Basic(elements);
		
		String renderF = renderToSparql(f), renderT = renderToSparql(SPA);
		String expected = "{" + renderF + " " + renderT + "}";
		StringBuilder sb = new StringBuilder();
		b.toSparql(new Settings(), sb);

		String result = sb.toString();
//		System.err.println(">> expected: " + expected);
//		System.err.println(">> obtained: " + result);	
		assertEquals(expected, result);
	}

}
