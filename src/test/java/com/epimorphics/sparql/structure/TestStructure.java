/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.structure;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.templates.Settings;

public class TestStructure {

	
	public static class Query {

		public String toSparql(Settings s) {
			StringBuilder sb = new StringBuilder();
			toSparql(s, sb);
			return sb.toString();
		}

		private void toSparql(Settings s, StringBuilder sb) {
			sb.append("select * where {}");
		}

		
		
	}
	
	@Test public void testEmptyQuery() {
		Query q = new Query();
		String result = q.toSparql(new Settings());
		assertEquals("select * where {}", result);
	}
	
	@Test public void testQuery() {
	}
	
	
}
