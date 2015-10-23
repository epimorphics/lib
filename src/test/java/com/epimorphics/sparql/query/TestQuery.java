/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import static org.junit.Assert.*;

import org.apache.jena.query.QueryFactory;
import org.junit.Test;

import com.epimorphics.sparql.exprs.Infix;
import com.epimorphics.sparql.exprs.LeafExprs;
import com.epimorphics.sparql.exprs.Op;
import com.epimorphics.sparql.graphpatterns.GraphPattern;
import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.query.Query;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsExpr;
import com.epimorphics.sparql.terms.TermAtomic;
import com.epimorphics.sparql.terms.Triple;
import com.epimorphics.sparql.terms.URI;
import com.epimorphics.sparql.terms.Filter;
import com.epimorphics.sparql.terms.Var;

import static com.epimorphics.sparql.exprs.LeafExprs.*;

import static com.epimorphics.test.utils.MakeCollection.*;

public class TestQuery {

	
	@Test public void testEmptySelectQuery() {
		Query q = new Query();
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT * WHERE {}", result);
	}
	
	@Test public void testEmptyConstructQuery() {
		Query q = new Query();
		String result = q.toSparqlConstruct(new Settings());
		assertEqualSparql("CONSTRUCT {} WHERE {}", result);
	}

	static final TermAtomic S = new URI("http://example.com/S");
	static final TermAtomic P = new URI("http://example.com/P");
	static final TermAtomic Q = new URI("http://example.com/Q");
	
	static final TermAtomic V = new Var("V");
	static final TermAtomic W = new Var("W");
	
	static final TermAtomic A = integer(17);
	
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
	
	@Test public void testQueryWithTriplePattern() {
		Query q = new Query();
		
		Filter filter = new Filter(LeafExprs.bool(true));
		GraphPattern where = new Basic(list(filter));
		
		q.setPattern(where);
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT * WHERE {FILTER(true)}", result);
	}
	
	@Test public void testQueryRespectsLimit() {
		Query q = new Query();
		q.setLimit(21);
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT * WHERE {} LIMIT 21", result);
	}
	
	@Test public void testQueryRespectsOffset() {
		Query q = new Query();
		q.setOffset(1066);
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT * WHERE {} OFFSET 1066", result);
	}
	
	@Test public void testQueryRespectsLimitAndOffset() {
		Query q = new Query();
		q.setLimit(21);
		q.setOffset(1829);
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT * WHERE {} LIMIT 21 OFFSET 1829", result);
	}
	
	@Test public void testSelectSingleVariableProjection() {
		Query q = new Query();
		q.addProjection(new Var("it"));
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT ?it WHERE {}", result);
	}
	
	@Test public void testSelectMultipleVariablesProjection() {
		Query q = new Query();
		q.addProjection(new Var("it"));
		q.addProjection(new Var("that"));
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT ?it ?that WHERE {}", result);
	}
	
	@Test public void testSelectBoundVariableProjection() {
		Query q = new Query();
		IsExpr e = new Var("e");
		Var it = new Var("it");
		q.addProjection(new As(e, it));
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT (?e AS ?it) WHERE {}", result);
	}
	
	@Test public void testSelectMixedProjection() {
		Query q = new Query();
		IsExpr e = new Var("e");
		Var it = new Var("it");
		q.addProjection(new Var("other"));
		q.addProjection(new As(e, it));
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT ?other (?e AS ?it) WHERE {}", result);
	}	
	
	@Test public void testOrderByClause() {
		Query q = new Query();
		q.addOrder(Order.ASC, new Var("it"));
		String result = q.toSparqlSelect(new Settings());
		String expected = "SELECT * WHERE {} ORDER BY ASC(?it)";
		assertEqualSparql(expected, result);
	}
	
	@Test public void testOrderByDESCClause() {
		Query q = new Query();
		q.addOrder(Order.DESC, new Var("it"));
		String result = q.toSparqlSelect(new Settings());
		String expected = "SELECT * WHERE {} ORDER BY DESC(?it)";
		assertEqualSparql(expected, result);
	}
	
	@Test public void testMultipleOrderByClauses() {
		Query q = new Query();
		Var A = new Var("A"), B = new Var("B");
		IsExpr e = new Infix(A, Op.opEq, B);
		q.addOrder(Order.DESC, new Var("it"));
		q.addOrder(Order.ASC, e);
		String result = q.toSparqlSelect(new Settings());
		String expected = "SELECT * WHERE {} ORDER BY DESC(?it) ASC(?A = ?B)";
		assertEqualSparql(expected, result);
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

	private void assertIn(String searchFor, String searchIn) {
		if (!searchIn.contains(searchFor)) {
			fail("The string \n'" + searchFor + "' should be present in\n'" + searchIn + "'");
		}
	}

	private void assertEqualSparql(String expected, String result) {
		QueryFactory.create(expected);
		QueryFactory.create(result);
		assertEquals(expected, result);
	}
	
}
