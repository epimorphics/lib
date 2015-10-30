/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import com.epimorphics.sparql.templates.Settings;

public class Exists implements PatternCommon, GraphPattern {

	final boolean exists;
	final GraphPattern P;
	
	public Exists(boolean exists, GraphPattern P) {
		this.exists = exists;
		this.P = P;
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append("IF");
		if (!exists) sb.append(" NOT");
		sb.append(" EXISTS ");
		P.toSparql(s, sb);
	}
	
}