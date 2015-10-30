/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static com.epimorphics.test.utils.MakeCollection.list;
import static com.epimorphics.util.SparqlUtils.renderToSparql;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.graphpatterns.GraphPattern;
import com.epimorphics.sparql.graphpatterns.Minus;
import com.epimorphics.sparql.terms.Triple;

public class TestMinusPatterns extends SharedFixtures {


	@Test public void testConstructMinusPattern() {
		Triple T = new Triple(S, P, A);
		Triple U = new Triple(S, Q, B);
		GraphPattern A = new Basic(list(T));
		GraphPattern B = new Basic(list(U));
		GraphPattern M = new Minus(A, B);
		String expected = 
			renderToSparql((A)) 
			+ " MINUS "
			+ renderToSparql(B)
			;
		String obtained = renderToSparql(M);
		assertEquals(expected, obtained);
	}
}
