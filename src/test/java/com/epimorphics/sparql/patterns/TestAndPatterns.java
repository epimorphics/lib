/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.graphpatterns.And;
import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.graphpatterns.Exists;
import com.epimorphics.sparql.graphpatterns.GraphPattern;
import com.epimorphics.sparql.terms.Triple;

import static com.epimorphics.test.utils.MakeCollection.*;

public class TestAndPatterns extends SharedFixtures {
	
	@Test public void testAndPatternSequencing() {
		GraphPattern G = new Basic(list(new Triple(S, P, V)));
		GraphPattern E = new Exists(false, G);
		GraphPattern A = new And(G, E);
		String expected = " " + toPatternString(G) + " " + toPatternString(E);
		String obtained = toPatternString(A);
//		System.err.println(">> expected: " + expected);
		
		assertEquals(expected, obtained);
	}
}
