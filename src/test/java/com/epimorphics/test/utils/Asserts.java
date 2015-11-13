/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.test.utils;

import static org.junit.Assert.*;

public class Asserts {

	public static void assertDiffer(Object unexpected, Object result) {
		if (result.equals(unexpected)) {
			fail("result value should not be equal to " + unexpected);
		}
	}

	// Assert that the substring appears in the subject.
	public static void assertContains(String subject, String substring) {
		if (subject.contains(substring)) return;
//		System.err.println(">> query: " + subject);
//		System.err.println(">> subst: " + substring);
		fail("query\n" + subject + "\nshould contain\n" + substring + "\n");
	}

}
