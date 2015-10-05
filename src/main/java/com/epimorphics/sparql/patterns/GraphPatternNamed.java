/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.TermURI;

public class GraphPatternNamed implements GraphPattern {

	final GraphPattern pattern;
	final TermURI graphName;
	
	public GraphPatternNamed(TermURI graphName, GraphPattern pattern) {
		this.graphName = graphName;
		this.pattern = pattern;
	}
	
	public GraphPattern getPattern() {
		return pattern;
	}
	
	public TermURI getGraphName() {
		return graphName;
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append("GRAPH ");
		graphName.toSparql(s, sb);
		sb.append(" ");
		pattern.toSparql(s, sb);
	}

}
