/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.epimorphics.sparql.graphpatterns.GraphPattern;
import com.epimorphics.sparql.graphpatterns.Select;
import com.epimorphics.sparql.query.Order;
import com.epimorphics.sparql.query.QueryShape;
import com.epimorphics.sparql.terms.Var;

import static com.epimorphics.util.SparqlUtils.renderToSparql;

public class TestNestedSelectPatterns extends SharedFixtures {
	
	@Test public void testNestedSelect() {
		
		QueryShape subQ = new QueryShape();
		subQ.addOrder(Order.ASC, new Var("W"));
		GraphPattern P = new Select(subQ);
		
		String expected = "{SELECT * WHERE {} ORDER BY ASC(?W)}";
		String obtained = renderToSparql(P);
		
		assertEquals(expected, obtained);
	}
	

}
