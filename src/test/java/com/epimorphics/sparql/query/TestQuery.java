/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import com.epimorphics.sparql.terms.*;
import org.apache.jena.vocabulary.XSD;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.epimorphics.sparql.exprs.LeafExprs;
import com.epimorphics.sparql.graphpatterns.GraphPattern;
import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.templates.Settings;

import static com.epimorphics.test.utils.MakeCollection.*;

public class TestQuery extends SharedFixtures {
	
	@Test public void testEmptySelectQuery() {
		QueryShape q = new QueryShape();
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT * WHERE {}", result);
	}

	@Test public void testRespectsRawModifier() {
		QueryShape q = new QueryShape();
		String fragment = "ORDER BY ?phone";
		assertSame(q, q.addRawModifier(fragment));
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT * WHERE {}_X".replace("_X", fragment), result);
	}
	
	@Test public void testRespectsDistinct() {
		QueryShape q = new QueryShape();
		q.setDistinction(Distinction.DISTINCT);
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT DISTINCT * WHERE {}", result);
	}

	@Test public void testRespectsReduced() {
		QueryShape q = new QueryShape();
		q.setDistinction(Distinction.REDUCED);
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT REDUCED * WHERE {}", result);
	}
	
	@Test public void testQueryWithTriplePattern() {
		QueryShape q = new QueryShape();
		
		Filter filter = new Filter(LeafExprs.bool(true));
		GraphPattern where = new Basic(list(filter));
		
		q.setEarlyPattern(where);
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT * WHERE {FILTER(true)}", result);
	}

	@Test public void testQueryTriplePatterns() {
		QueryShape q = new QueryShape();
		Var v = new Var("s");
		URI p = new URI("http://example.com/p");
		Triple t1 = new Triple(v, p, new Literal("foo", null, ""));
		Triple t2 = new Triple(v, p, new Literal("42", new URI(XSD.integer.getURI()), ""));
		Triple t3 = new Triple(v, p, new Literal("foo'bar", null, ""));
		GraphPattern where = new Basic(list(t1, t2, t3));
		q.setEarlyPattern(where);
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT * WHERE {?s <http://example.com/p> 'foo' . ?s <http://example.com/p> 42 . ?s <http://example.com/p> 'foo\\'bar' .}", result);
	}
	
	@Test public void testPrefixGeneration() {
		QueryShape q = new QueryShape();
		Settings s = new Settings();
		s.setPrefix("ex", "http://localhost/exemplar/");
		
		TermAtomic S = new URI("http://localhost/exemplar/S");
		TermAtomic P = new URI("http://localhost/exemplar/O");
		TermAtomic O = new URI("http://localhost/exemplar/P");
		q.addEarlyPattern(new Basic(new Triple(S, P, O)));
		String result = q.toSparqlSelect(s);
		
		assertTrue(s.getUsedPrefixes().contains("ex"));
		assertIn("PREFIX ex: <http://localhost/exemplar/>", result);	
	}

	
}
