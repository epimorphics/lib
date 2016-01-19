/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/

package com.epimorphics.sparql.terms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.epimorphics.sparql.templates.Settings;

public class TermList implements IsSparqler, TermAtomic {

	final List<TermAtomic> terms;

	public TermList(List<TermAtomic> terms) {
		this.terms = terms;
	}
	
	public TermList(TermAtomic ... terms) {
		this(Arrays.asList(terms));
	}
	
	@Override public int hashCode() {
		return terms.hashCode();
	}
	
	@Override public boolean equals(Object other) {
		return other instanceof TermList && terms.equals(((TermList)other).terms);
	}
	
	public static TermList fromNumbers(List<Number> values) {
		List<TermAtomic> result = new ArrayList<TermAtomic>();
		for (Number v: values) result.add(Literal.fromNumber(v));
		return new TermList(result);
	}
	
	@Override public String toString() {
		StringBuilder sb = new StringBuilder();
		toSparql(new Settings(), sb);
		return sb.toString();
	}
	
	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append("(");
		String before = "";
		for (TermAtomic t: terms) {
			sb.append(before); before = " ";
			t.toSparql(s, sb);
		}
		sb.append(")");
	}
	
}