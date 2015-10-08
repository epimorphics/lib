/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import java.util.Arrays;
import java.util.List;

import com.epimorphics.sparql.templates.Settings;

public class Basic implements GraphPattern {
	
	final List<Common> elements;
	
	public Basic(List<Common> elements) {
		this.elements = elements;
	}	
	
	public Basic(Common... elements) {
		this.elements = Arrays.asList(elements);
	}
	
	public List<Common> getElements() {
		return elements;
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		String gap = "";
		sb.append("{");
		for (Common p: elements) {
			sb.append(gap);
			gap = " ";
			p.toSparql(s, sb);
		}
		sb.append("}");
	}
	
	
}
