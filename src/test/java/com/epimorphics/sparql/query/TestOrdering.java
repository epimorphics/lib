/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import org.junit.Test;

import com.epimorphics.sparql.exprs.Infix;
import com.epimorphics.sparql.exprs.Op;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsExpr;
import com.epimorphics.sparql.terms.Var;

public class TestOrdering extends SharedFixtures {
	
	@Test public void testOrderByClause() {
		AbstractSparqlQuery q = new AbstractSparqlQuery();
		q.addOrder(Order.ASC, new Var("it"));
		String result = q.toSparqlSelect(new Settings());
		String expected = "SELECT * WHERE {} ORDER BY ASC(?it)";
		assertEqualSparql(expected, result);
	}
	
	@Test public void testOrderByDESCClause() {
		AbstractSparqlQuery q = new AbstractSparqlQuery();
		q.addOrder(Order.DESC, new Var("it"));
		String result = q.toSparqlSelect(new Settings());
		String expected = "SELECT * WHERE {} ORDER BY DESC(?it)";
		assertEqualSparql(expected, result);
	}
	
	@Test public void testMultipleOrderByClauses() {
		AbstractSparqlQuery q = new AbstractSparqlQuery();
		Var A = new Var("A"), B = new Var("B");
		IsExpr e = new Infix(A, Op.opEq, B);
		q.addOrder(Order.DESC, new Var("it"));
		q.addOrder(Order.ASC, e);
		String result = q.toSparqlSelect(new Settings());
		String expected = "SELECT * WHERE {} ORDER BY DESC(?it) ASC(?A = ?B)";
		assertEqualSparql(expected, result);
	}
	

}
