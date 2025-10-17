/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.templates;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestSettingsPrefixes {

//	@Test public void testSettingsPrefixes() {
//		Settings s = new Settings();
//		s.setPrefix("pre", "http://example.com/pre/");
//		assertEquals("pre:a", s.usePrefix("http://example.com/pre/a"));
//		assertEquals("pre:b", s.usePrefix("http://example.com/pre/b"));
//	}
	
	@Test public void testFullLength() {
		Settings s = new Settings();
		s.setPrefix("pre", "http://example.com/pre/");
		s.setPrefix("alt", "http://example.com/alt#");
			
		assertUnchanged(s, "http://example.com/pre/alpha/beta");
		assertUnchanged(s, "http://example.com/pre/alpha!beta");
		assertUnchanged(s, "http://example.com/pre/alpha[beta");
		assertUnchanged(s, "http://example.com/pre/-alpha");
		assertUnchanged(s, "http://example.com/pre/alpha-");
		assertUnchanged(s, "http://example.com/pre/.alpha");
		assertUnchanged(s, "http://example.com/pre/alpha.");
		assertUnchanged(s, "http://example.com/pre/:alpha");
		assertUnchanged(s, "http://example.com/pre/-alpha:");
		
		assertShortens(s, "pre:alpha.zog", "http://example.com/pre/alpha.zog");

		assertShortens(s, "pre:alpha", "http://example.com/pre/alpha");
		assertShortens(s, "pre:1066", "http://example.com/pre/1066");
		assertShortens(s, "pre:alphaBet", "http://example.com/pre/alphaBet");
		assertShortens(s, "pre:alpha.zog", "http://example.com/pre/alpha.zog");
		assertShortens(s, "pre:alpha:zog", "http://example.com/pre/alpha:zog");
		assertShortens(s, "pre:alpha-zog", "http://example.com/pre/alpha-zog");
		assertShortens(s, "pre:alpha", "http://example.com/pre/alpha");
	}
	
	void assertShortens(Settings s, String expected, String subject) {
		assertEquals(expected, s.usePrefix(subject));
	}

	void assertUnchanged(Settings s, String URI) {
		assertEquals(URI, s.usePrefix(URI));
	}	


}
