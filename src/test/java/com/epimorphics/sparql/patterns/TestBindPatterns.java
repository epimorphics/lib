/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

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
		
		String obtained = toPatternString(b);
		String expected = "BIND(?Expression AS ?x)";
		assertEquals(expected, obtained);
	}
	
}
