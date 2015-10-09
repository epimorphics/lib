/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import com.epimorphics.sparql.templates.Settings;

public final class Empty implements GraphPattern {
	
	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append("{}");
	}
}