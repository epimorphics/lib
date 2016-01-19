/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epmorphics.webapi.dispatch;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.webapi.dispatch.CompiledTemplate;

public class TestCompiledTemplate {

	// string to test :: bad literal fragment expected
	String [] illegalTemplates = new String[] {
			"/{name}/is{illegal        :: /is{illegal",
			"/this/is{illegal/{name}   :: /this/is{illegal/",
			"/{name}/is}ille{name}gal  :: /is}ille",
			"/{name}/is}ille{name}gal  :: /is}ille",
	};
	
	@Test public void testTrapsBadSyntax() {
		for (String pairString: illegalTemplates) {
			String [] pair = pairString.split(" *:: *");
			String template = pair[0], badFragment = pair[1];
			try { 
				CompiledTemplate.prepare(template, "irrelevant"); 
				fail("Should spot illegal syntax in '" + template + "'");
			} catch (CompiledTemplate.SyntaxError e) {
				assertEquals(badFragment, e.fragment);
			}
		}
	}
}
