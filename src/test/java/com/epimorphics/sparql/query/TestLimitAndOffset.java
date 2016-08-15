/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import org.junit.Test;

import com.epimorphics.sparql.templates.Settings;

public class TestLimitAndOffset extends SharedFixtures {
	
	@Test public void testQueryRespectsLimit() {
		QueryShape q = new QueryShape();
		q.setLimit(21);
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT * WHERE {} LIMIT 21", result);
	}
	
	@Test public void testQueryRespectsOffset() {
		QueryShape q = new QueryShape();
		q.setOffset(1066);
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT * WHERE {} OFFSET 1066", result);
	}
	
	@Test public void testQueryRespectsLimitAndOffset() {
		QueryShape q = new QueryShape();
		q.setLimit(21);
		q.setOffset(1829);
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT * WHERE {} LIMIT 21 OFFSET 1829", result);
	}
}
