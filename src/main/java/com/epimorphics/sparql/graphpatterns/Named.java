/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import org.apache.jena.shared.BrokenException;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.URI;

public class Named extends GraphPattern {

	final GraphPattern pattern;
	final URI graphName;
	
	public Named(URI graphName, GraphPattern pattern) {
		this.graphName = graphName;
		this.pattern = pattern;
	}
	
	public GraphPattern getPattern() {
		return pattern;
	}
	
	public URI getGraphName() {
		return graphName;
	}

	@Override public void toPatternString(Rank ignored, Settings s, StringBuilder sb) {
		sb.append("GRAPH ");
		graphName.toSparql(s, sb);
		sb.append(" ");
		sb.append("{");
		pattern.toPatternString(Rank.Zero, s, sb);
		sb.append("}");
	}

	@Override protected int ordinal() {
		return Rank.Named.ordinal();
	}

	@Override public void toPatternString(Settings s, StringBuilder sb) {
		throw new BrokenException("not reachable");
	}

}
