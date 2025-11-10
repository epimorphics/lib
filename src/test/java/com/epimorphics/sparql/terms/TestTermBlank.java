/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.epimorphics.util.SparqlUtils;

import static com.epimorphics.util.Asserts.*;

public class TestTermBlank {
	
	@Test public void testBlankTerms() {
		Blank ba = new Blank("ziggy"), bb = new Blank("ziggy");
		Blank c = new Blank("soggy");
		assertEquals(ba, bb);
		assertDiffer(c, ba);
		Blank b1 = new Blank(), b2 = new Blank();
		assertDiffer(b1, b2);
	}
	
	@Test public void testBlankRendering() {
		Blank b = new Blank("left");
		assertEquals("_:left", SparqlUtils.renderToSparql(b));
				
	}
}
