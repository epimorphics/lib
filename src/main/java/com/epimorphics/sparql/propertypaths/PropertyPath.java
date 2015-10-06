/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/

package com.epimorphics.sparql.propertypaths;

import com.epimorphics.sparql.terms.TermAtomic;

public interface PropertyPath extends TermAtomic {
	
	public static enum Repeat {
		OPTIONAL, ZEROMORE, ONEMORE;
		
		@Override public String toString() {
			if (this == OPTIONAL) return "?";
			if (this == ZEROMORE) return "*";
			if (this == ONEMORE) return "+";
			return "UNKNOWN";				
		}
	}
}