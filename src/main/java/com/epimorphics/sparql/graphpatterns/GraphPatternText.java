/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/

package com.epimorphics.sparql.graphpatterns;

import com.epimorphics.sparql.templates.Settings;

/**
	A GraphPatternText is a graph pattern that contains arbitrary text
	that is substituted directly into the SPARQL query.
*/
public class GraphPatternText implements PatternCommon, GraphPattern {

	final String text;
	
	public GraphPatternText(String text) {
		this.text = text;
	}
	
	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append(text);
	}

}
