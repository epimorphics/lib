/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import com.epimorphics.sparql.templates.Settings;

public class Exists extends GraphPattern  {

	final boolean exists;
	final GraphPattern P;
	
	public Exists(boolean exists, GraphPattern P) {
		this.exists = exists;
		this.P = P;
	}

	@Override public void toPatternString(Settings s, StringBuilder sb) {
		sb.append("IF");
		if (!exists) sb.append(" NOT");
		sb.append(" EXISTS ");
		sb.append("{");
		P.toPatternString(Rank.Zero, s, sb);
		sb.append("}");
	}

	@Override protected int ordinal() {
		return Rank.Exists.ordinal();
	}
	
}