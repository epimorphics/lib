/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static com.epimorphics.test.utils.MakeCollection.list;
import static com.epimorphics.util.SparqlUtils.basicPattern;
import static com.epimorphics.util.SparqlUtils.renderToSparql;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.epimorphics.sparql.graphpatterns.GraphPattern;
import com.epimorphics.sparql.graphpatterns.Union;
import com.epimorphics.sparql.terms.Triple;

public class TestUnionPatterns extends SharedFixtures {
	
	@Test public void testUnionPatternToSparql() {
		
		GraphPattern x = basicPattern(new Triple(A, P, A));
		GraphPattern y = basicPattern(new Triple(A, Q, B));
		
		Union u = new Union(x, y);
		
		assertEquals(list(x, y), u.getPatterns());
		
		String xRendering = renderToSparql(x);
		String yRendering = renderToSparql(y);
		
		String expected = "{" + xRendering + " UNION " + yRendering + "}";
		String unionResult = renderToSparql(u);
		
		assertEquals(expected, unionResult);
	}
	
}
