/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.graphpatterns.Exists;
import com.epimorphics.sparql.graphpatterns.GraphPattern;
import com.epimorphics.sparql.terms.Triple;

import static com.epimorphics.util.SparqlUtils.*;
import static com.epimorphics.test.utils.MakeCollection.*;

public class TestExistsPatterns extends SharedFixtures {

	@Test public void testIfNotExists() {
		GraphPattern G = new Basic(list(new Triple(S, P, V)));
		GraphPattern E = new Exists(false, G);
		String expected =
			"IF NOT EXISTS {" + renderToSparql(G) + "}"
			;
		String obtained = renderToSparql(E);
		assertEquals(expected, obtained);
	}
	
	@Test public void testIfExists() {
		GraphPattern G = new Basic(list(new Triple(S, P, V)));
		GraphPattern E = new Exists(true, G);
		String expected =
			"IF EXISTS {" + renderToSparql(G) + "}"
			;
		String obtained = renderToSparql(E);
//		System.err.println(">> expected: " + expected);
//		System.err.println(">> obtained: " + obtained);
		assertEquals(expected, obtained);
	}
	
}
