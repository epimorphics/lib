/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.test.utils;

import static org.junit.Assert.*;

public class Asserts {

	public static void assertDiffer(Object unexpected, Object result) {
		if (result.equals(unexpected)) {
			fail("result value should not be equal to " + unexpected);
		}
	}

}
