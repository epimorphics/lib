/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.structure;

import org.junit.Test;

public class TestStructure {

	
	public static class Expr {
		
	}
	
	public static class Query {

		public void setDistinct(boolean b) {
			
		}

		public void setLimitAndOffset(int limit, int offset) {
			
		}

//		public void addPattern(GraphPattern gp) {
//			
//		}

		public void addOrder(Expr expr, boolean upwards) {
			
		}
		
	}
	
	@Test public void testQuery() {
		Query s = new Query();
		
		s.setDistinct(true);
		s.setLimitAndOffset(1, 2);
		s.addOrder(new Expr(), true);
//		s.setConstruct();
//		s.setProject();
//		s.addPattern(new GraphPattern());
	}
	
	
}
