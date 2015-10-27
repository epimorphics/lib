/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import org.junit.Test;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.Triple;

public class TestConstructQueries extends SharedFixtures{
	
	@Test public void testEmptyConstructQuery() {
		Query q = new Query();
		String result = q.toSparqlConstruct(new Settings());
		assertEqualSparql("CONSTRUCT {} WHERE {}", result);
	}

	
	@Test public void testNonemptyConstructQuery() {
		Query q = new Query();
		q.construct(new Triple(S, P, V));
		String result = q.toSparqlConstruct(new Settings());
		String expected = 
			"CONSTRUCT {_S _P ?V .} WHERE {}"
			.replace("_S", S.toString())
			.replace("_P", P.toString())
			;
		assertEqualSparql(expected, result);
	}
	
	@Test public void testMultipleTriplesNonemptyConstructQuery() {
		Query q = new Query();
		q.construct(new Triple(S, P, V));
		q.construct(new Triple(S, Q, W));
		String result = q.toSparqlConstruct(new Settings());
		String expected = 
			"CONSTRUCT {_S _P ?V . _S _Q ?W .} WHERE {}"
			.replace("_S", S.toString())
			.replace("_P", P.toString())
			.replace("_Q", Q.toString())
			;
		assertEqualSparql(expected, result);
	}
}
