/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/

package com.epimorphics.sparql.propertypaths;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.TermURI;

public class PropertyPathProperty extends PropertyPathBase implements PropertyPath {

	final TermURI property;
	
	public PropertyPathProperty(TermURI property) {
		this.property = property;
	}
	
	@Override public void coreToSparql(Settings s, StringBuilder sb) {
		property.toSparql(s, sb);
	}

	@Override public int precedence() {
		return PROP_PRECEDENCE;
	}
	
}