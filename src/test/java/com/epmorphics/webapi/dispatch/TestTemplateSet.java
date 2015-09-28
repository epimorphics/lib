/*
    See lda-top/LICENCE (or https://raw.github.com/epimorphics/elda/master/LICENCE)
    for the licence for this software.
    
    (c) Copyright 2011 Epimorphics Limited
    $Id$
*/
package com.epmorphics.webapi.dispatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.AbstractMultivaluedMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.epimorphics.test.utils.MakeCollection;
import com.epimorphics.test.utils.MakeHash;
import com.epimorphics.webapi.dispatch.TemplateSet;
import com.epimorphics.webapi.dispatch.CompiledTemplate;

public class TestTemplateSet {
	
	@Test public void testTemplateSorting() {
		TemplateSet<String> s = new TemplateSet<String>();
		String path_A = "/def/hospital/Hospital/instances";
		String path_B = "/def/hospital/Hospital/instances/lat/{lat}/long/{long}";
		s.register( path_A, "A" );
		s.register( path_B, "B" );
		assertEquals( MakeCollection.list(path_B, path_A), s.templates() );
	}
	
	
	@Test public void ensure_searcher_finds_correct_value() {
		String path1 = "/abc/def", path2 = "/abc/{xyz}", path3 = "/other", path4 = "/abc/{x}{y}{z}";
		TemplateSet<String> r = new TemplateSet<String>();
		r.register(path3, "A" ); 
		r.register(path4, "B" );
		r.register(path2, "C" ); 
		r.register(path1, "D" ); 
		Map<String, String> b = new HashMap<String, String>();
		assertEquals("D", r.lookup(b, "/abc/def", null) );
		assertEquals("C", r.lookup(b, "/abc/27", null ) );
		assertEquals("A", r.lookup(b, "/other", null ) );
	}
	
	@Test public void ensure_matching_for_fixed_query_parameters() {
		TemplateSet<String> r = new TemplateSet<String>();
		Map<String, String> bindings = new HashMap<String, String>();
		MultivaluedMap<String, String> params = makeMultiMap();
		r.register( "/anchor?k=v", "A" );
	//
		assertEquals( null, r.lookup( bindings, "/anchor", params ) );
		params.add( "k", "v" );
		assertEquals( "A", r.lookup( bindings, "/anchor", params ) );
	}
	
	
	@Test public void testing_template_with_big_query_parameter_patterns() {
		TemplateSet<String> r = new TemplateSet<String>();
		r.register("/eggs?arg={V}", "VAL"); 
	//		
		Map<String, String> b = new HashMap<String, String>();
		MultivaluedMap<String, String> params = makeMultiMap();
		params.add("arg", "JHEREG");
	//		
		assertEquals("VAL", r.lookup(b, "/eggs", params) );
		assertEquals("JHEREG", b.get("V"));
	}
	
	@Test public void testing_template_with_big_multiple_parameter_patterns() {
		TemplateSet<String> r = new TemplateSet<String>();
		r.register("/comma?arg={X},{Y}", "VAL"); 
		
		Map<String, String> b = new HashMap<String, String>();
		MultivaluedMap<String, String> params = makeMultiMap();
		params.add("arg", "314,271");
		
		assertEquals("VAL", r.lookup(b, "/comma", params) );
		
		assertEquals("314", b.get("X"));
		assertEquals("271", b.get("Y"));
	}
	
	@Test public void testing_template_with_big_multiple_parameter_patterns2() {
		TemplateSet<String> r = new TemplateSet<String>();
		r.register("/comma?arg=x:{X},y:{Y}", "VAL"); 
		
		Map<String, String> b = new HashMap<String, String>();
		MultivaluedMap<String, String> params = makeMultiMap();
		params.add("arg", "x:314,y:271");
		
		assertEquals("VAL", r.lookup(b, "/comma", params) );
		
		assertEquals("314", b.get("X"));
		assertEquals("271", b.get("Y"));
	}
	
	@Test public void testing_template_with_big_multiple_parameter_patterns3() {
		TemplateSet<String> r = new TemplateSet<String>();
		r.register("/comma?arg=x:{X},y:{Y}&other={O}", "OTHER"); 
		
		Map<String, String> b = new HashMap<String, String>();
		MultivaluedMap<String, String> params = makeMultiMap();
		params.add("arg", "x:314,y:271");
		params.add("other", "1829");
		
		assertEquals("OTHER", r.lookup(b, "/comma", params) );
		
		assertEquals("314", b.get("X"));
		assertEquals("271", b.get("Y"));
		assertEquals("1829", b.get("O"));
	}
	
	@Test public void ensure_matching_for_variable_query_parameters() {
		TemplateSet<String> r = new TemplateSet<String>();
		Map<String, String> bindings = new HashMap<String, String>();
		MultivaluedMap<String, String> params = makeMultiMap();
		r.register( "/anchor?k={v}", "A" );
	//
		assertEquals( null, r.lookup( bindings, "/anchor", params ) );
		params.add( "k", "value" );
		assertEquals( "A", r.lookup( bindings, "/anchor", params ) );
		assertEquals( "value", bindings.get( "v" ) );
	}
	
	private MultivaluedMap<String, String> makeMultiMap() {
		Map<String, List<String>> basis = new HashMap<String, List<String>>();
		return new AbstractMultivaluedMap<String, String>(basis) {};				
	}


	@Test public void ensure_MatchSearcher_can_remove_template() {		
		Map<String, String> b = new HashMap<String, String>();
		TemplateSet<String> r = new TemplateSet<String>();
		String path = "/going/away/";
		r.register( path, "GA" );
		assertEquals( "GA", r.lookup( b, path, null ) );
		r.unregister( path );
		assertEquals( null, r.lookup( b, path, null ) );
	}
	
	@Test public void ensure_matching_captures_variables() {
		String template = "/furber/any-{alpha}-{beta}/{gamma}";
		String uri = "/furber/any-99-100/boggle";
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> expected = MakeHash.hashMap( "alpha=99 beta=100 gamma=boggle" );
		CompiledTemplate<String> ut = CompiledTemplate.prepare( template, "SPOO" );
		assertTrue( "the uri should match the pattern", ut.match(map, uri, null ) );
		assertEquals( expected, map );
	}
	
}
