/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import java.util.Arrays;
import java.util.List;

import com.epimorphics.sparql.templates.Settings;

public class And implements PatternCommon, GraphPattern {

	final List<GraphPattern> elements;
	
	public And(GraphPattern ... elements) {
		this(Arrays.asList(elements));
	}
	
	public And(List<GraphPattern> elements) {
		this.elements = elements;
	}
	
	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append("{");
		String before = "";
		for (GraphPattern e: elements) {
			sb.append(before); before = " ";
			e.toSparql(s, sb);
		}
		sb.append("}");
	}

}
