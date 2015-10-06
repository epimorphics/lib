/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.expr.LeafExprs;
import com.epimorphics.sparql.patterns.GraphPattern;
import com.epimorphics.sparql.patterns.GraphPatternBasic;
import com.epimorphics.sparql.query.Query;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.TermExpr;
import com.epimorphics.sparql.terms.TermFilter;
import com.epimorphics.sparql.terms.TermVar;

import static com.epimorphics.test.utils.MakeCollection.*;

public class TestQuery {

	
	@Test public void testEmptyQuery() {
		Query q = new Query();
		String result = q.toSparql(new Settings());
		assertEquals("SELECT * WHERE {}", result);
	}
	
	@Test public void testQueryWithTriplePattern() {
		Query q = new Query();
		
		TermFilter filter = new TermFilter(LeafExprs.bool(true));
		GraphPattern where = new GraphPatternBasic(list(filter));
		
		q.setPattern(where);
		String result = q.toSparql(new Settings());
		assertEquals("SELECT * WHERE {FILTER(true)}", result);
	}
	
	@Test public void testQueryRespectsLimit() {
		Query q = new Query();
		q.setLimit(21);
		String result = q.toSparql(new Settings());
		assertEquals("SELECT * WHERE {} LIMIT 21", result);
	}
	
	@Test public void testQueryRespectsOffset() {
		Query q = new Query();
		q.setOffset(1066);
		String result = q.toSparql(new Settings());
		assertEquals("SELECT * WHERE {} OFFSET 1066", result);
	}
	
	@Test public void testQueryRespectsLimitAndOffset() {
		Query q = new Query();
		q.setLimit(21);
		q.setOffset(1829);
		String result = q.toSparql(new Settings());
		assertEquals("SELECT * WHERE {} LIMIT 21 OFFSET 1829", result);
	}
	
	@Test public void testSelectSingleVariableProjection() {
		Query q = new Query();
		q.addProjection(new TermVar("it"));
		String result = q.toSparql(new Settings());
		assertEquals("SELECT ?it WHERE {}", result);
	}
	
	@Test public void testSelectMultipleVariablesProjection() {
		Query q = new Query();
		q.addProjection(new TermVar("it"));
		q.addProjection(new TermVar("that"));
		String result = q.toSparql(new Settings());
		assertEquals("SELECT ?it ?that WHERE {}", result);
	}
	
	@Test public void testSelectBoundVariableProjection() {
		Query q = new Query();
		TermExpr e = new TermVar("e");
		TermVar it = new TermVar("it");
		q.addProjection(new TermAs(e, it));
		String result = q.toSparql(new Settings());
		assertEquals("SELECT (?e AS ?it) WHERE {}", result);
	}
	
	@Test public void testSelectMixedProjection() {
		Query q = new Query();
		TermExpr e = new TermVar("e");
		TermVar it = new TermVar("it");
		q.addProjection(new TermVar("other"));
		q.addProjection(new TermAs(e, it));
		String result = q.toSparql(new Settings());
		assertEquals("SELECT ?other (?e AS ?it) WHERE {}", result);
	}	
	
}
