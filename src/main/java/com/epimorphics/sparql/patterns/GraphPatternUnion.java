/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import java.util.ArrayList;
import java.util.List;

import com.epimorphics.sparql.templates.Settings;

public class GraphPatternUnion implements GraphPattern {

	final List<GraphPattern> args = new ArrayList<GraphPattern>();
	
	public GraphPatternUnion(GraphPattern... args) {
		for (GraphPattern p: args) this.args.add(p);
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		String between = "";
		sb.append("{");
		for (GraphPattern a: args) {
			sb.append(between);
			a.toSparql(s, sb);
			between = " UNION ";
		}
		sb.append("}");
	}

}
