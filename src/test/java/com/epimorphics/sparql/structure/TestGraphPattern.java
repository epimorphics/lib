/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.structure;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.epimorphics.sparql.terms.TermLiteral;
import com.epimorphics.sparql.terms.TermSparql;
import com.epimorphics.sparql.terms.TermTriple;
import com.epimorphics.sparql.terms.TermURI;
import com.epimorphics.test.utils.MakeCollection;

public class TestGraphPattern {

	static class GraphPatternBuilder {

		final List<TermSparql> elements = new ArrayList<TermSparql>();
		
		public void addTriple(TermTriple t) {
			elements.add(t);
		}

		public GraphPattern build() {
			return new GraphPattern(new ArrayList<TermSparql>(elements));
		}
		
	}
	
	static class GraphPattern {

		final List<TermSparql> elements;
		
		public GraphPattern(List<TermSparql> elements) {
			this.elements = elements;
		}
		
		public List<TermSparql> elements() {
			return elements;
		}
		
	}
	
	static final TermURI type = new TermURI("http://example.com/type/T");
	
	static final TermSparql S = new TermURI("http://example.com/S");
	static final TermSparql P = new TermURI("http://example.com/P");
	static final TermSparql Q = new TermURI("http://example.com/Q");
	static final TermSparql A = new TermLiteral("17", type, "");
	static final TermSparql B = new TermLiteral("chat", type, "");
	
	@Test public void testBasicTriplesPattern() {
		GraphPatternBuilder b = new GraphPatternBuilder();
		TermTriple SPA = new TermTriple(S, P, A);
		TermTriple SQB = new TermTriple(S, Q, B);

		b.addTriple(SPA);
		b.addTriple(SQB);
		GraphPattern gp = b.build();
		
		assertEquals(MakeCollection.list(SPA, SQB), gp.elements());
	}
}
