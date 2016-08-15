/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import java.util.ArrayList;
import java.util.List;

import com.epimorphics.sparql.terms.TripleOrFilter;

public class Builder {

	final List<TripleOrFilter> elements = new ArrayList<TripleOrFilter>();
	
	public void addElement(TripleOrFilter t) {
		elements.add(t);
	}

	public Basic build() {
		return new Basic(new ArrayList<TripleOrFilter>(elements));
	}
	
}