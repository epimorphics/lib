/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.templates;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestSettingsPrefixes {

	@Test public void testSettingsPrefixes() {
		Settings s = new Settings();
		s.setPrefix("pre", "http://example.com/pre/");
		assertEquals("pre:a", s.usePrefix("http://example.com/pre/a"));
		assertEquals("pre:b", s.usePrefix("http://example.com/pre/b"));
	}
}
