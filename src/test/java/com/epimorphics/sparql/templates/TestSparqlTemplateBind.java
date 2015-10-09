/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.templates;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.terms.Var;

public class TestSparqlTemplateBind {

	@Test public void testExpandTemplate() {
		Template t = new Template("($alpha)");
		
		Settings s = new Settings();
		s.putParam("alpha", new Var("v"));
		
		String result = t.substWith(s);
		
		assertEquals("(?v)", result);
	}
}
