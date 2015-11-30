/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.util;

import java.util.Arrays;
import java.util.List;

public class ListUtils {

	/**
		list(x...) returns a list(T) whose elements are x...		
	*/
	@SafeVarargs public static <T> List<T> list( T... elements ) {
		return Arrays.asList( elements );
	}
}
