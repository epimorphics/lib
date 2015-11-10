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

	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append("OPTIONAL {");
		operand.toSparql(s, sb);
		sb.append("}");
	}

	public GraphPattern getPattern() {
		return operand;
	}

}
