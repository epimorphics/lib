/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static com.epimorphics.util.SparqlUtils.renderToSparql;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.epimorphics.sparql.graphpatterns.Bind;
import com.epimorphics.sparql.terms.IsExpr;
import com.epimorphics.sparql.terms.Var;

public class TestBindPatterns extends SharedFixtures {

	@Test public void testConstructBindPattern() {
		Var x = new Var("x");
		IsExpr e = new Var("Expression");
		Bind b = new Bind(e, x);
		
		assertSame(x, b.getVar());
		assertSame(e, b.getExpr());
		
		String obtained = renderToSparql(b);
		String expected = "BIND(?Expression AS ?x)";
		assertEquals(expected, obtained);
	}
	
}
