/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.graphpatterns.Builder;
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
		Filter f = new Filter(new Literal("17", Literal.xsdInteger, ""));
		Triple SPA = new Triple(S, P, A);
		Basic b = new Basic(f, SPA);
		
		String renderF = renderToSparql(f), renderT = renderToSparql(SPA);
		String expected = renderF + " " + renderT;
		StringBuilder sb = new StringBuilder();
		b.toSparql(new Settings(), sb);

		String obtained = sb.toString();
//		System.err.println(">> expected: " + expected);
//		System.err.println(">> obtained: " + obtained);	
		assertEquals(expected, obtained);
	}

}
