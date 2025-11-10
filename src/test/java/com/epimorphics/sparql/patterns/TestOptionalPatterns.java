/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.graphpatterns.GraphPattern;
import com.epimorphics.sparql.graphpatterns.Optional;
import com.epimorphics.sparql.query.QueryShape;
import com.epimorphics.sparql.terms.Triple;
import com.epimorphics.sparql.terms.TripleOrFilter;

import static com.epimorphics.test.utils.MakeCollection.*;

public class TestOptionalPatterns extends SharedFixtures {
	
	@Test public void testOptionalPatternToSparql() {
		
		TripleOrFilter x = new Triple(S, P, A);
		List<TripleOrFilter> elements = list(x);
		GraphPattern operand = new Basic(elements);
		Optional g = new Optional(operand);
		
		assertEquals(operand, g.getPattern());
		
		String basicResult = toPatternString(operand);
		String optionalResult = toPatternString(g);
		
		assertEquals("OPTIONAL {" + basicResult + "}", optionalResult);		
	}
	
	@Test public void testOptionalPatternToFullSparql() {
		
		QueryShape q = new QueryShape();
		
		TripleOrFilter x = new Triple(S, P, A);
		List<TripleOrFilter> elements = list(x);
		GraphPattern operand = new Basic(elements);
		Optional g = new Optional(operand);
		
		q.addEarlyPattern(g);
				
		String basicResult = toPatternString(operand);
		String optionalResult = toPatternString(g);

		String expected = "OPTIONAL {" + basicResult + "}";
		
//		System.err.println(">> expected: " + expected);
//		System.err.println(">> obtained: " + optionalResult);
		
		assertEquals(expected, optionalResult);		
	}

	@Test public void testNestedOptionalPatternToFullSparql() {
		
		QueryShape q = new QueryShape();
		
		TripleOrFilter x = new Triple(S, P, A);
		List<TripleOrFilter> elements = list(x);
		GraphPattern operand = new Basic(elements);
		Optional g = new Optional(operand);
		Optional g2 = new Optional(g);
		
		q.addEarlyPattern(g2);
				
		String optionalResult = toPatternString(g2);
		
		String expected = "OPTIONAL {OPTIONAL {<http://example.com/S> <http://example.com/P> 17 .}}";
		
//		System.err.println(">> expected: " + expected);
//		System.err.println(">> obtained: " + optionalResult);
		
		assertEquals(expected, optionalResult);		
	}
}
