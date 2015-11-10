/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import java.util.Arrays;
import java.util.List;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.TripleOrFilter;

public class Basic extends GraphPattern {
	
	final List<TripleOrFilter> elements;
	
	public Basic(List<TripleOrFilter> elements) {
		this.elements = elements;
	}	
	
	public Basic(TripleOrFilter... elements) {
		this.elements = Arrays.asList(elements);
	}
	
	public List<TripleOrFilter> getElements() {
		return elements;
	}

	@Override public void toSparqlWrapped(Settings s, StringBuilder sb) {
		String gap = "";
		for (TripleOrFilter p: elements) {
			sb.append(gap);
			gap = " ";
			p.toSparql(s, sb);
		}
	}
	
	
}
