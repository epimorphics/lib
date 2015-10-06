/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/

package com.epimorphics.sparql.propertypaths;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.TestTermTriple;

public class PropertyPathRep implements PropertyPath {

	final PropertyPath path;
	final PropertyPath.Repeat rep;
	
	public PropertyPathRep(PropertyPath path, PropertyPath.Repeat rep) {
		this.path = path;
		this.rep = rep;
	}
	
	@Override public void toSparql(Settings s, StringBuilder sb) {
		path.toSparql(s, sb);
		sb.append(rep);
	}
	
}