/******************************************************************
 * File:        TestNameUtils.java
 * Created by:  Dave Reynolds
 * Created on:  12 Apr 2011
 * 
 * (c) Copyright 2011, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.util;

import junit.framework.TestCase;
import static com.epimorphics.util.NameUtils.*;

public class TestNameUtils extends TestCase {

	public void testEncode() {
		assertEquals("urn%2ffoo", encodeSafeName("urn/foo") );
		doTestEncode("urn/foo");
		doTestEncode("foo-bar");
		doTestEncode("http://www.epimorphics.com/foo#bar?baz");
	}
	
	private void doTestEncode(String test) {
		String enc = encodeSafeName(test);
		assertEquals(test, decodeSafeName(enc));
	}
}
