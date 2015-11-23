/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static org.junit.Assert.*;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Test;

import com.epimorphics.sparql.geo.GeoQuery;
import com.epimorphics.sparql.query.AbstractSparqlQuery;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.Var;
import com.epimorphics.util.Asserts;
import com.epimorphics.util.SparqlUtils;

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
	
	@Test public void testGeoRendering() {
		
		Property spatial_withinBox = ResourceFactory.createProperty("eh:/P");
		GeoQuery.register(GeoQuery.withinBox, spatial_withinBox);
		
		AbstractSparqlQuery q = new AbstractSparqlQuery();
		double r = 10.0, x = 1.2, y = 2.1;
		GeoQuery gq = new GeoQuery(GeoQuery.withinBox, r, x, y);
		q.setGeoQuery(new Var("it"), gq);
		
		StringBuilder sb = new StringBuilder();
		Settings s = new Settings();
		String obtained = q.toSparqlSelect(s);
		
//		Asserts.assertContains(obtained, "?it spatial:withinBox (10, 1.2, 2.1)");
//		
//		System.err.println(">>\n" + obtained);
		
	}
}
