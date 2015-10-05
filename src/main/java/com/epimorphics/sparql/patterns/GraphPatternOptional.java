/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import com.epimorphics.sparql.templates.Settings;

public class GraphPatternOptional implements GraphPattern {

	final GraphPattern operand;
	
	public GraphPatternOptional(GraphPattern operand) {
		this.operand = operand;
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append("OPTIONAL {");
		operand.toSparql(s, sb);
		sb.append("}");
	}

}
