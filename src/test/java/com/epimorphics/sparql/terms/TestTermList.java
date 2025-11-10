/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/

package com.epimorphics.sparql.terms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.epimorphics.util.SparqlUtils;

public class TestTermList {

	@Test public void testEmptyTermList() {
		TermList t = new TermList();
		String result = SparqlUtils.renderToSparql(t);
		assertEquals( "()", result );
	}
	
	static final TermAtomic S = new URI("http://example.com/S");
	static final TermAtomic V = new Var("V");
	
	@Test public void testNonEmptyTermList() {
		TermList t = new TermList(S, V);
		String result = SparqlUtils.renderToSparql(t);
		String s = SparqlUtils.renderToSparql(S);
		String v = SparqlUtils.renderToSparql(V);
		assertEquals( "(" + s + " " + v + ")", result );
	}
}
