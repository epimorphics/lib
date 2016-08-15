/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import java.util.ArrayList;
import java.util.List;

import com.epimorphics.sparql.templates.Settings;

public class Union extends GraphPattern {

	final List<GraphPattern> patterns = new ArrayList<GraphPattern>();
	
	public Union(GraphPattern... args) {
		for (GraphPattern p: args) this.patterns.add(p);
	}
	
	public List<GraphPattern> getPatterns() {
		return patterns;
	}

	@Override public void toPatternString(Settings s, StringBuilder sb) {
		String between = "";
		for (GraphPattern a: patterns) {
			sb.append(between);
			a.toPatternString(Rank.Union, s, sb);
			between = " UNION ";
		}
	}

	@Override protected int ordinal() {
		return Rank.Union.ordinal();
	}

}
