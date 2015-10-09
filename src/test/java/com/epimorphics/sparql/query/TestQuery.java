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
import com.epimorphics.sparql.terms.Filter;
import com.epimorphics.sparql.terms.Var;

import static com.epimorphics.test.utils.MakeCollection.*;

public class TestQuery {

	
	@Test public void testEmptyQuery() {
		Query q = new Query();
		String result = q.toSparql(new Settings());
		assertEqualSparql("SELECT * WHERE {}", result);
	}
	
	@Test public void testQueryWithTriplePattern() {
		Query q = new Query();
		
		Filter filter = new Filter(LeafExprs.bool(true));
		GraphPattern where = new Basic(list(filter));
		
		q.setPattern(where);
		String result = q.toSparql(new Settings());
		assertEqualSparql("SELECT * WHERE {FILTER(true)}", result);
	}
	
	@Test public void testQueryRespectsLimit() {
		Query q = new Query();
		q.setLimit(21);
		String result = q.toSparql(new Settings());
		assertEqualSparql("SELECT * WHERE {} LIMIT 21", result);
	}
	
	@Test public void testQueryRespectsOffset() {
		Query q = new Query();
		q.setOffset(1066);
		String result = q.toSparql(new Settings());
		assertEqualSparql("SELECT * WHERE {} OFFSET 1066", result);
	}
	
	@Test public void testQueryRespectsLimitAndOffset() {
		Query q = new Query();
		q.setLimit(21);
		q.setOffset(1829);
		String result = q.toSparql(new Settings());
		assertEqualSparql("SELECT * WHERE {} LIMIT 21 OFFSET 1829", result);
	}
	
	@Test public void testSelectSingleVariableProjection() {
		Query q = new Query();
		q.addProjection(new Var("it"));
		String result = q.toSparql(new Settings());
		assertEqualSparql("SELECT ?it WHERE {}", result);
	}
	
	@Test public void testSelectMultipleVariablesProjection() {
		Query q = new Query();
		q.addProjection(new Var("it"));
		q.addProjection(new Var("that"));
		String result = q.toSparql(new Settings());
		assertEqualSparql("SELECT ?it ?that WHERE {}", result);
	}
	
	@Test public void testSelectBoundVariableProjection() {
		Query q = new Query();
		IsExpr e = new Var("e");
		Var it = new Var("it");
		q.addProjection(new As(e, it));
		String result = q.toSparql(new Settings());
		assertEqualSparql("SELECT (?e AS ?it) WHERE {}", result);
	}
	
	@Test public void testSelectMixedProjection() {
		Query q = new Query();
		IsExpr e = new Var("e");
		Var it = new Var("it");
		q.addProjection(new Var("other"));
		q.addProjection(new As(e, it));
		String result = q.toSparql(new Settings());
		assertEqualSparql("SELECT ?other (?e AS ?it) WHERE {}", result);
	}	
	
	@Test public void testOrderByClause() {
		Query q = new Query();
		q.addOrder(Order.ASC, new Var("it"));
		String result = q.toSparql(new Settings());
		String expected = "SELECT * WHERE {} ORDER BY ASC(?it)";
		assertEqualSparql(expected, result);
	}
	
	@Test public void testOrderByDESCClause() {
		Query q = new Query();
		q.addOrder(Order.DESC, new Var("it"));
		String result = q.toSparql(new Settings());
		String expected = "SELECT * WHERE {} ORDER BY DESC(?it)";
		assertEqualSparql(expected, result);
	}
	
	@Test public void testMultipleOrderByClauses() {
		Query q = new Query();
		Var A = new Var("A"), B = new Var("B");
		IsExpr e = new Infix(A, Op.opEq, B);
		q.addOrder(Order.DESC, new Var("it"));
		q.addOrder(Order.ASC, e);
		String result = q.toSparql(new Settings());
		String expected = "SELECT * WHERE {} ORDER BY DESC(?it) ASC(?A = ?B)";
		assertEqualSparql(expected, result);
	}

	private void assertEqualSparql(String expected, String result) {
		QueryFactory.create(expected);
		QueryFactory.create(result);
		assertEquals(expected, result);
	}
	
}
