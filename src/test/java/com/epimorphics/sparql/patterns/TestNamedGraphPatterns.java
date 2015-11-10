/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.graphpatterns.GraphPattern;
import com.epimorphics.sparql.graphpatterns.Named;
import com.epimorphics.sparql.terms.Triple;
import com.epimorphics.sparql.terms.URI;

import static com.epimorphics.util.SparqlUtils.*;

public class TestNamedGraphPatterns extends SharedFixtures {

	@Test public void testNamedGraphToSparql() {
		
		URI graph = new URI("http://example.com/graph");
		GraphPattern pattern = new Basic(new Triple(A, P, A));
		Named n = new Named(graph, pattern);
		assertSame(graph, n.getGraphName());
		assertSame(pattern, n.getPattern());
		
		String expected = "GRAPH " + renderToSparql(graph) + " {" + renderToSparql(pattern) + "}";
		String obtained = renderToSparql(n);
//		System.err.println(">> expected: " + expected);
//		System.err.println(">> obtained: " + obtained);
		assertEquals(expected, obtained);
	}
}
