/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import static org.junit.Assert.*;

import org.junit.Test;

import static com.epimorphics.test.utils.Asserts.*;

public class TestTermVar {
	
	@Test public void testVariable() {
		TermVar tv1 = new TermVar("item");
		
		assertEquals("item", tv1.getSpelling());
		assertEquals("item", tv1.getName());
		assertEquals("?item", tv1.toString());
		
		assertEquals(tv1, new TermVar("item"));
		assertDiffer(tv1, new TermVar("meti"));
		
		assertEquals("item".hashCode(), tv1.hashCode());
	}
}
