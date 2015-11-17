/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import java.util.Arrays;
import java.util.List;

import com.epimorphics.sparql.templates.Settings;

public class And extends GraphPattern {

	final List<GraphPattern> elements;
	
	public And(GraphPattern... args) {
		this(Arrays.asList(args));
	}
	
	public And(List<GraphPattern> args) {
		this.elements = args;
	}

	@Override public void toSparqlWrapped(Settings s, StringBuilder sb) {
		if (elements.size() == 1) {
			elements.get(0).toSparql(s, sb);
		} else {
			for (GraphPattern g: elements) {
				sb.append(" ");
				g.toSparqlWrapped(s, sb);
			}
		}
	}
	
	@Override public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("And{");
		for (GraphPattern g: elements) sb.append(g);
		sb.append("}");
		return sb.toString();
	}

}
