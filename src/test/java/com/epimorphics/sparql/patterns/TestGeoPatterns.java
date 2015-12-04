/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.geo.GeoQuery;
import com.epimorphics.sparql.query.QueryShape;
import com.epimorphics.sparql.query.Transforms;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.Var;
import com.epimorphics.util.Asserts;

import static com.epimorphics.util.Asserts.*;

public class TestGeoPatterns extends SharedFixtures {

	static final Var alpha = new Var("alpha");
	static final Var beta = new Var("beta");
	
	@Test public void testGeoEquality() {
		GeoQuery q = new GeoQuery(alpha, "name", 1.0, 2.0);
	//
		assertEquals(q, new GeoQuery(alpha, "name", 1.0, 2.0));
		assertDiffer(q, new GeoQuery(alpha, "eman", 1.0, 2.0));
		assertDiffer(q, new GeoQuery(alpha, "name", 20, 2.0));
		assertDiffer(q, new GeoQuery(alpha, "name", 1.0, 4.0));
		assertDiffer(q, new GeoQuery(beta, "name", 1.0, 4.0));
	}
	
	@Test public void testAddGeoQuery() {
		QueryShape q = new QueryShape();
		assertNull(q.getGeoQuery());
	//
		double r = 10.0, x = 1.2, y = 2.1;
		GeoQuery gq = new GeoQuery(alpha, GeoQuery.withinBox, r, x, y);
		q.setGeoQuery(gq);
		assertEquals(gq, q.getGeoQuery());
	}
	
	@Test public void testGeoRenderingLikeJenaSpatial() {

		QueryShape q = new QueryShape(); 
		q.getTransforms().add(GeoQuery.byIndex);
		
		Settings s = new Settings()
			.setPrefix("spatial", GeoQuery.spatial)
			;
				
		double r = 10.0, x = 1.2, y = 2.1;
		GeoQuery gq = new GeoQuery(alpha, GeoQuery.withinBox, r, x, y);
		q.setGeoQuery(gq);
		
//		StringBuilder sb = new StringBuilder();
		String obtained = q.toSparqlSelect(s);
		
		Asserts.assertContains(obtained, "?alpha spatial:withinBox (10.0 1.2 2.1)");
		
//		System.err.println(">>\n" + obtained);
		
	}
	
}
