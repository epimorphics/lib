/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.geo.GeoQuery;
import com.epimorphics.sparql.query.AbstractSparqlQuery;
import com.epimorphics.sparql.terms.Var;
import static com.epimorphics.util.Asserts.*;

public class TestGeoPatterns extends SharedFixtures {

	@Test public void testGeoEquality() {
		GeoQuery q = new GeoQuery("name", 1.0, 2.0);
	//
		assertEquals(q, new GeoQuery("name", 1.0, 2.0));
		assertDiffer(q, new GeoQuery("eman", 1.0, 2.0));
		assertDiffer(q, new GeoQuery("name", 20, 2.0));
		assertDiffer(q, new GeoQuery("name", 1.0, 4.0));
	}
	
	@Test public void testAddGeoQuery() {
		AbstractSparqlQuery q = new AbstractSparqlQuery();
		assertNull(q.getGeoQuery());
	//
		double r = 10.0, x = 1.2, y = 2.1;
		GeoQuery gq = new GeoQuery(GeoQuery.withinBox, r, x, y);
		q.setGeoQuery(new Var("it"), gq);
		assertEquals(gq, q.getGeoQuery());
	}
}
