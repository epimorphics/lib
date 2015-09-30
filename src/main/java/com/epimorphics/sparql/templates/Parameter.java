/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.templates;

public class Parameter implements Element {

	public static final String USUAL = null;
	
	final String spelling;
	final String type;
	
	Parameter(String spelling, String type) {
		this.spelling = spelling;
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
}