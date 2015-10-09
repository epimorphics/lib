/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import com.epimorphics.sparql.templates.Settings;

public class Var implements IsExpr, TermAtomic, IsSparqler, Projection {
	
	final String name;
	
	public Var(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return "?" + name;
	}
	
	public boolean equals(Object other) {
		return other instanceof Var && same((Var) other);
	}

	private boolean same(Var other) {
		return name.equals(other.name);
	}
	
	public int hashCode() {
		return name.hashCode();
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append("?").append(name);
	}
	
	@Override public void toSparql(int precedence, Settings s, StringBuilder sb) {
		toSparql(s, sb);
	}
}
