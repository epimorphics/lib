/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import java.util.ArrayList;
import java.util.List;

public class Builder {

	final List<PatternCommon> elements = new ArrayList<PatternCommon>();
	
	public void addElement(PatternCommon t) {
		elements.add(t);
	}

	public Basic build() {
		return new Basic(new ArrayList<PatternCommon>(elements));
	}
	
}