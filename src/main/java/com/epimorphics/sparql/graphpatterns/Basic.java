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

	public String toString() {
		return "Basic{" + elements + "}";
	}
	
	@Override public int hashCode() {
		return elements.hashCode();
	}
	
	@Override public boolean equals(Object other) {
		return other instanceof Basic && elements.equals(((Basic) other).elements);
	}
	
	@Override public void toPatternString(Settings s, StringBuilder sb) {
		String gap = "";
		for (TripleOrFilter p: elements) {
			sb.append(gap);
			gap = " ";
			p.toSparql(s, sb);
		}
	}

	@Override protected int ordinal() {
		return Rank.Basic.ordinal();
	}
	
	
}
