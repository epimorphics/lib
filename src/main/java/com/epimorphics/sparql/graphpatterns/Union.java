/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import java.util.ArrayList;
import java.util.List;

import com.epimorphics.sparql.templates.Settings;

public class Union implements GraphPattern {

	final List<GraphPattern> patterns = new ArrayList<GraphPattern>();
	
	public Union(GraphPattern... args) {
		for (GraphPattern p: args) this.patterns.add(p);
	}
	
	public List<GraphPattern> getPatterns() {
		return patterns;
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		String between = "";
		sb.append("{");
		for (GraphPattern a: patterns) {
			sb.append(between);
			a.toSparql(s, sb);
			between = " UNION ";
		}
		sb.append("}");
	}

}