/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import java.util.List;

public class GraphPatternBasic implements GraphPattern {
	final List<PatternBase> elements;
	
	public GraphPatternBasic(List<PatternBase> elements) {
		this.elements = elements;
	}
	
	public List<PatternBase> elements() {
		return elements;
	}
	
}
