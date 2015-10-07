/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import java.util.ArrayList;
import java.util.List;

public class Builder {

	final List<Common> elements = new ArrayList<Common>();
	
	public void addElement(Common t) {
		elements.add(t);
	}

	public Basic build() {
		return new Basic(new ArrayList<Common>(elements));
	}
	
}