/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

public class TermVar implements TermAtomic {

	final String spelling;
	
	public TermVar(String spelling) {
		this.spelling = spelling;
	}

	public String getSpelling() {
		return spelling;
	}

	public String getName() {
		return spelling;
	}

	public String toString() {
		return "?" + spelling;
	}
	
	public boolean equals(Object other) {
		return other instanceof TermVar && same((TermVar) other);
	}

	private boolean same(TermVar other) {
		return spelling.equals(other.spelling);
	}
	
	public int hashCode() {
		return spelling.hashCode();
	}
}
