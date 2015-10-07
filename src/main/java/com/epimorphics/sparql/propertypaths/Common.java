/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.propertypaths;

import com.epimorphics.sparql.templates.Settings;

public abstract class Common implements PropertyPath {

	@Override public void toSparql(Settings s, StringBuilder sb) {
		toSparql(OUTER_PRECEDENCE, s, sb);
	}
	
	public void toSparql(int precedence, Settings s, StringBuilder sb) {
		if (precedence() < precedence) sb.append("(");
		coreToSparql(s, sb);
		if (precedence() < precedence) sb.append(")");
	}
	
	public abstract int precedence();
	
	public abstract void coreToSparql(Settings s, StringBuilder sb);

}
