/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.templates;

import com.epimorphics.sparql.terms.Spelling;
import com.epimorphics.sparql.terms.TermSparql;

public class Parameter extends Spelling implements Element {

	public static final String USUAL = null;
	
	final String type;
	
	public Parameter(String spelling, String type) {
		super(spelling);
		this.type = type;
	}
	
	public boolean equals(Object other) {
		return other instanceof Parameter && same((Parameter) other);
	}
	
	public String toString() {
		return "par(" + spelling + ")";
	}

	private boolean same(Parameter other) {
		return spelling.equals(other.spelling);
	}

	@Override public void subst(StringBuilder sb, Settings s) {
		TermSparql ts = s.getParam(spelling);
		ts.toSparql(s, sb);
	}
}