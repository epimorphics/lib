/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import static com.epimorphics.sparql.exprs.LeafExprs.integer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.jena.query.QueryFactory;

import com.epimorphics.sparql.terms.TermAtomic;
import com.epimorphics.sparql.terms.URI;
import com.epimorphics.sparql.terms.Var;

public class SharedFixtures {

	static final TermAtomic S = new URI("http://example.com/S");
	static final TermAtomic P = new URI("http://example.com/P");
	static final TermAtomic Q = new URI("http://example.com/Q");
	
	static final TermAtomic V = new Var("V");
	static final TermAtomic W = new Var("W");
	
	static final TermAtomic A = integer(17);
	
	protected static void assertIn(String searchFor, String searchIn) {
		if (!searchIn.contains(searchFor)) {
			fail("The string \n'" + searchFor + "' should be present in\n'" + searchIn + "'");
		}
	}

	protected static void assertEqualSparql(String expected, String result) {
		QueryFactory.create(expected);
		QueryFactory.create(result);
		assertEquals(expected, result);
	}
}
