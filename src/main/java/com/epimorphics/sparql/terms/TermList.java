/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/

package com.epimorphics.sparql.terms;

import java.util.Arrays;
import java.util.List;

import com.epimorphics.sparql.graphpatterns.PatternCommon;
import com.epimorphics.sparql.templates.Settings;

public class TermList implements PatternCommon, IsSparqler {

	final List<TermAtomic> terms;
	
	public TermList(TermAtomic ... terms) {
		this.terms = Arrays.asList(terms);
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