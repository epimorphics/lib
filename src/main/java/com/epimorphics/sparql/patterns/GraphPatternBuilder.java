/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import java.util.ArrayList;
import java.util.List;

public class GraphPatternBuilder {

	final List<PatternBase> elements = new ArrayList<PatternBase>();
	
	public void addElement(PatternBase t) {
		elements.add(t);
	}

	public GraphPatternBasic build() {
		return new GraphPatternBasic(new ArrayList<PatternBase>(elements));
	}
	
}