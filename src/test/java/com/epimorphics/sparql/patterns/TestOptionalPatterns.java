/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.graphpatterns.GraphPattern;
import com.epimorphics.sparql.graphpatterns.Optional;
import com.epimorphics.sparql.query.Query;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.Triple;
import com.epimorphics.sparql.terms.TripleOrFilter;

import static com.epimorphics.util.SparqlUtils.*;
import static com.epimorphics.test.utils.MakeCollection.*;

public class TestOptionalPatterns extends SharedFixtures {
	
	@Test public void testOptionalPatternToSparql() {
		
		TripleOrFilter x = new Triple(S, P, A);
		List<TripleOrFilter> elements = list(x);
		GraphPattern operand = new Basic(elements);
		Optional g = new Optional(operand);
		
		assertEquals(operand, g.getPattern());
		
		String basicResult = renderToSparql(operand);
		String optionalResult = renderToSparql(g);
		
		assertEquals("OPTIONAL {" + basicResult + "}", optionalResult);		
	}
	
	@Test public void testOptionalPatternToFullSparql() {
		
		Query q = new Query();
		
		TripleOrFilter x = new Triple(S, P, A);
		List<TripleOrFilter> elements = list(x);
		GraphPattern operand = new Basic(elements);
		Optional g = new Optional(operand);
		
		q.addEarlyPattern(g);
				
		String basicResult = renderToSparql(operand);
		String optionalResult = q.toSparqlSelect(new Settings());
		
		assertEquals("SELECT * WHERE {OPTIONAL {" + basicResult + "}}", optionalResult);		
	}
	
	@Test public void testNestecOptionalPatternToFullSparql() {
		
		Query q = new Query();
		
		TripleOrFilter x = new Triple(S, P, A);
		List<TripleOrFilter> elements = list(x);
		GraphPattern operand = new Basic(elements);
		Optional g = new Optional(operand);
		Optional g2 = new Optional(g);
		
		q.addEarlyPattern(g2);
				
		String optionalResult = q.toSparqlSelect(new Settings());
		
		String expected = "SELECT * WHERE {OPTIONAL {OPTIONAL {<http://example.com/S> <http://example.com/P> 17 .}}}";
		
//		System.err.println(">> expected: " + expected);
//		System.err.println(">> obtained: " + optionalResult);
		
		assertEquals(expected, optionalResult);		
	}
}
