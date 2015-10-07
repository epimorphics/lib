/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/

package com.epimorphics.sparql.propertypaths;

import com.epimorphics.sparql.templates.Settings;

public class Inv extends Common implements PropertyPath {

	final PropertyPath path;
	
	public Inv(PropertyPath path) {
		this.path = path;
	}
	
	@Override public void coreToSparql(Settings s, StringBuilder sb) {
		sb.append("^");
		path.toSparql(precedence(), s, sb);
	}

	@Override public int precedence() {
		return INV_PRECEDENCE;
	}
	
}