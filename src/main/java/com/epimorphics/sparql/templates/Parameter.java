/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.templates;

import com.epimorphics.sparql.terms.IsSparqler;

public class Parameter implements Element {

	public static final String USUAL = null;
	
	final String name;
	final String type;
	
	public Parameter(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public boolean equals(Object other) {
		return other instanceof Parameter && same((Parameter) other);
	}
	
	public String toString() {
		return "par(" + name + ")";
	}

	private boolean same(Parameter other) {
		return name.equals(other.name);
	}

	@Override public void subst(StringBuilder sb, Settings s) {
		IsSparqler ts = s.getParam(name);
		ts.toSparql(s, sb);
	}
}