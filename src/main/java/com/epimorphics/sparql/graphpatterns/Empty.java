/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import com.epimorphics.sparql.templates.Settings;

public final class Empty extends GraphPattern {

	@Override public void toPatternString(Settings s, StringBuilder sb) {
		sb.append("");		
	}

	@Override protected int ordinal() {
		return Rank.Empty.ordinal();
	}
}