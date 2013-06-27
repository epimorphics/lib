/******************************************************************
 * File:        TestNameUtils.java
 * Created by:  Dave Reynolds
 * Created on:  12 Apr 2011
 * 
 * (c) Copyright 2011, Epimorphics Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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
