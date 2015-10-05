/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import java.util.List;

import com.epimorphics.sparql.templates.Settings;

public class GraphPatternBasic implements GraphPattern {
	final List<PatternBase> elements;
	
	public GraphPatternBasic(List<PatternBase> elements) {
		this.elements = elements;
	}
	
	public List<PatternBase> elements() {
		return elements;
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		String gap = "";
		for (PatternBase p: elements) {
			sb.append(gap);
			gap = " ";
			p.toSparql(s, sb);
		}
	}
	
	
}
