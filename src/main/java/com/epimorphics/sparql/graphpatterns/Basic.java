/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import java.util.Arrays;
import java.util.List;

import com.epimorphics.sparql.templates.Settings;

public class Basic implements GraphPattern {
	
	final List<PatternCommon> elements;
	
	public Basic(List<PatternCommon> elements) {
		this.elements = elements;
	}	
	
	public Basic(PatternCommon... elements) {
		this.elements = Arrays.asList(elements);
	}
	
	public List<PatternCommon> getElements() {
		return elements;
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		String gap = "";
		sb.append("{");
		for (PatternCommon p: elements) {
			sb.append(gap);
			gap = " ";
			p.toSparql(s, sb);
		}
		sb.append("}");
	}
	
	
}
