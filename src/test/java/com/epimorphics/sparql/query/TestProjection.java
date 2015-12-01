/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import org.junit.Test;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsExpr;
import com.epimorphics.sparql.terms.Var;

public class TestProjection extends SharedFixtures {
	
	@Test public void testSelectSingleVariableProjection() {
		QueryShape q = new QueryShape();
		q.addProjection(new Var("it"));
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT ?it WHERE {}", result);
	}
	
	@Test public void testSelectMultipleVariablesProjection() {
		QueryShape q = new QueryShape();
		q.addProjection(new Var("it"));
		q.addProjection(new Var("that"));
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT ?it ?that WHERE {}", result);
	}
	
	@Test public void testSelectBoundVariableProjection() {
		QueryShape q = new QueryShape();
		IsExpr e = new Var("e");
		Var it = new Var("it");
		q.addProjection(new As(e, it));
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT (?e AS ?it) WHERE {}", result);
	}
	
	@Test public void testSelectMixedProjection() {
		QueryShape q = new QueryShape();
		IsExpr e = new Var("e");
		Var it = new Var("it");
		q.addProjection(new Var("other"));
		q.addProjection(new As(e, it));
		String result = q.toSparqlSelect(new Settings());
		assertEqualSparql("SELECT ?other (?e AS ?it) WHERE {}", result);
	}	
}
