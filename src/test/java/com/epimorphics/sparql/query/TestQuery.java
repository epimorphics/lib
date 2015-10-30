/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import org.junit.Test;
import static org.junit.Assert.*;

import com.epimorphics.sparql.exprs.LeafExprs;
import com.epimorphics.sparql.graphpatterns.GraphPattern;
import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.query.Query;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.TermAtomic;
import com.epimorphics.sparql.terms.Triple;
import com.epimorphics.sparql.terms.URI;
import com.epimorphics.sparql.terms.Filter;

import static com.epimorphics.test.utils.MakeCollection.*;

public class TestQuery extends SharedFixtures {
	
	@Test public void testEmptySelectQuery() {
		Query q = new Query();
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT * WHERE {}", result);
	}

	@Test public void testRespectsDistinct() {
		Query q = new Query();
		q.setDistinction(Query.Distinction.DISTINCT);
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT DISTINCT * WHERE {}", result);
	}

	@Test public void testRespectsReduced() {
		Query q = new Query();
		q.setDistinction(Query.Distinction.REDUCED);
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT REDUCED * WHERE {}", result);
	}
	
	@Test public void testQueryWithTriplePattern() {
		Query q = new Query();
		
		Filter filter = new Filter(LeafExprs.bool(true));
		GraphPattern where = new Basic(list(filter));
		
		q.setPattern(where);
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT * WHERE {FILTER(true)}", result);
	}
	
	@Test public void testPrefixGeneration() {
		Query q = new Query();
		Settings s = new Settings();
		s.setPrefix("ex", "http://localhost/exemplar/");
		
		TermAtomic S = new URI("http://localhost/exemplar/S");
		TermAtomic P = new URI("http://localhost/exemplar/O");
		TermAtomic O = new URI("http://localhost/exemplar/P");
		q.addPattern(new Basic(new Triple(S, P, O)));
		String result = q.toSparqlSelect(s);
		
		assertTrue(s.getUsedPrefixes().contains("ex"));
		assertIn("PREFIX ex: <http://localhost/exemplar/>", result);	
	}

	
}
