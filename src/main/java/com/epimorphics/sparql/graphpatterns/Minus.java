/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import com.epimorphics.sparql.templates.Settings;

public class Minus extends GraphPattern {

	final GraphPattern A;
	final GraphPattern B;
	
	public Minus(GraphPattern A, GraphPattern B) {
		this.A = A;
		this.B = B;
	}

	@Override public void toPatternString(Settings s, StringBuilder sb) {
		A.toPatternString(Rank.Max, s, sb);
		sb.append(" MINUS ");
		B.toPatternString(Rank.Max, s, sb);
	}

	@Override protected int ordinal() {
		return Rank.Minus.ordinal();
	}
}