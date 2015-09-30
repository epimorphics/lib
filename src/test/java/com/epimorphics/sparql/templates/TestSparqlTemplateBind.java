/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.templates;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.epimorphics.sparql.terms.TermSparql;
import com.epimorphics.sparql.terms.TermVar;

public class TestSparqlTemplateBind {

	@Test public void testExpandTemplate() {
		Template t = new Template("($alpha)");
		
		Map<String, TermSparql> bindings = new HashMap<String, TermSparql>();
		bindings.put("alpha", new TermVar("v"));
		
		String result = t.substWith(bindings);
		
		assertEquals("(?v)", result);
	}
}
