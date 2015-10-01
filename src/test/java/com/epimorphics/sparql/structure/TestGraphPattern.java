/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.structure;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.patterns.GraphPatternBasic;
import com.epimorphics.sparql.patterns.GraphPatternBuilder;
import com.epimorphics.sparql.terms.TermLiteral;
import com.epimorphics.sparql.terms.TermSparql;
import com.epimorphics.sparql.terms.TermTriple;
import com.epimorphics.sparql.terms.TermURI;
import com.epimorphics.test.utils.MakeCollection;

public class TestGraphPattern {

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

		b.addElement(SPA);
		b.addElement(SQB);
		GraphPatternBasic gp = b.build();
		
		assertEquals(MakeCollection.list(SPA, SQB), gp.elements());
	}
}
