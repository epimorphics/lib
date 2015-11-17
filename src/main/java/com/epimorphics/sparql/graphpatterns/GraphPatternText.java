/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/

package com.epimorphics.sparql.graphpatterns;

import com.epimorphics.sparql.templates.Settings;

/**
	A GraphPatternText is a graph pattern that contains arbitrary text
	that is substituted directly into the SPARQL query.
*/
public class GraphPatternText extends GraphPattern {

	final String text;
	
	public GraphPatternText(String text) {
		this.text = text;
	}

	@Override public void toSparqlWrapped(Settings s, StringBuilder sb) {
		sb.append(text);
	}
	
	public String toString() {
		return "Text{" + text + "}";
	}

}
