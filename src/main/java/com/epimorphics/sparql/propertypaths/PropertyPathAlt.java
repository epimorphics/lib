/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/

package com.epimorphics.sparql.propertypaths;

import java.util.Arrays;
import java.util.List;

import com.epimorphics.sparql.templates.Settings;

public class PropertyPathAlt extends PropertyPathBase implements PropertyPath {

	final List<PropertyPath> paths;
	
	public PropertyPathAlt(PropertyPath ... paths) {
		this.paths = Arrays.asList(paths);
	}
	
	@Override public void coreToSparql(Settings s, StringBuilder sb) {
		if (paths.size() == 1) {
			paths.get(0).toSparql(precedence(), s, sb);
		} else {
			String sep = "";
			for (PropertyPath p: paths) {
				sb.append(sep);
				sep = "|";
				p.toSparql(s, sb);
			}
		}
	}

	@Override public int precedence() {
		return ALT_PRECEDENCE;
	}
	
}