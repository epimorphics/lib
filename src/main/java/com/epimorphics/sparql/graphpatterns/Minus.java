/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import com.epimorphics.sparql.templates.Settings;

public class Minus implements PatternCommon, GraphPattern {

	final GraphPattern A;
	final GraphPattern B;
	
	public Minus(GraphPattern A, GraphPattern B) {
		this.A = A;
		this.B = B;
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		A.toSparql(s, sb);
		sb.append(" MINUS ");
		B.toSparql(s, sb);
	}
	
}