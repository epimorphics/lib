/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/

package com.epimorphics.sparql.propertypaths;

import com.epimorphics.sparql.templates.Settings;

public class PropertyPathInv extends PropertyPathBase implements PropertyPath {

	final PropertyPath path;
	
	public PropertyPathInv(PropertyPath path) {
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