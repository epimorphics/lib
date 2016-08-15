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

	public void toPatternString(Settings s, StringBuilder sb) {
		sb.append("OPTIONAL {");
		operand.toPatternString(Rank.NoBraces, s, sb);
		sb.append("}");
	}

	@Override protected int ordinal() {
		return Rank.Optional.ordinal();
	}

}
