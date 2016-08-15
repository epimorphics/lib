/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/

package com.epimorphics.sparql.propertypaths;

import com.epimorphics.sparql.templates.Settings;
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
	
	public static int OUTER_PRECEDENCE = 0;
	
	public static int ALT_PRECEDENCE = 1;
	
	public static int SEQ_PRECEDENCE = 2;
	
	public static int INV_PRECEDENCE = 3;
	
	public static int REP_PRECEDENCE = 4;
	
	public static int PROP_PRECEDENCE = 5;
	
	public void toSparql(int precedence, Settings s, StringBuilder sb);
}