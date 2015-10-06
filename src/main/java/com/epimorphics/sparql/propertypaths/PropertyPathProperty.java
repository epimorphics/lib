/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/

package com.epimorphics.sparql.propertypaths;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.TermURI;

public class PropertyPathProperty implements PropertyPath {

	final TermURI property;
	
	public PropertyPathProperty(TermURI property) {
		this.property = property;
	}
	
	@Override public void toSparql(Settings s, StringBuilder sb) {
		property.toSparql(s, sb);
	}
	
}