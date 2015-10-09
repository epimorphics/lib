/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/

package com.epimorphics.sparql.propertypaths;

import com.epimorphics.sparql.templates.Settings;

public class Rep extends PathCommon implements PropertyPath {

	final PropertyPath path;
	final PropertyPath.Repeat rep;
	
	public Rep(PropertyPath path, PropertyPath.Repeat rep) {
		this.path = path;
		this.rep = rep;
	}
	
	@Override public void coreToSparql(Settings s, StringBuilder sb) {
		path.toSparql(precedence(), s, sb);
		sb.append(rep);
	}

	@Override public int precedence() {
		return REP_PRECEDENCE;
	}
	
}