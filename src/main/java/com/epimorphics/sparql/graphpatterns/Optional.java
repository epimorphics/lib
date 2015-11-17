/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import com.epimorphics.sparql.templates.Settings;

public class Optional extends GraphPattern {

	final GraphPattern operand;
	
	public Optional(GraphPattern operand) {
		this.operand = operand;
	}

	public GraphPattern getPattern() {
		return operand;
	}

	@Override public void toSparqlUnWrapped(Settings s, StringBuilder sb) {
		eitherSparql(s, sb);
	}

	@Override public void toSparqlWrapped(Settings s, StringBuilder sb) {
		eitherSparql(s, sb);
	}

	private void eitherSparql(Settings s, StringBuilder sb) {
		sb.append("OPTIONAL ");
		sb.append("{");
		operand.toSparqlWrapped(s, sb);
		sb.append("}");
	}

}
