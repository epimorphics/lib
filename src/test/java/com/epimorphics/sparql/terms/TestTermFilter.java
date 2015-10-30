/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.templates.Settings;

public class TestTermFilter {

	@Test public void testTermFilter() {
		IsExpr e = new IsExpr() { 
			
			public String toString() {return "E";}

			@Override public void toSparql(Settings s, StringBuilder sb) {
				sb.append("E");
			}
			
			@Override public void toSparql(int precedence, Settings s, StringBuilder sb) {
				toSparql(s, sb);
			}
			
		};
		
		Filter tf = new Filter(e);
		assertEquals(e, tf.getExpr());
		assertEquals(tf, new Filter(e));
		StringBuilder sb = new StringBuilder();
		tf.toSparql(new Settings(), sb);
		assertEquals("FILTER(E)", sb.toString());
	}
}
