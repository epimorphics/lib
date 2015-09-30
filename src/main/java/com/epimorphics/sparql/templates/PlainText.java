/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.templates;

public class PlainText implements Element {
	
	final String spelling;
	
	PlainText(String spelling) {
		this.spelling = spelling;
	}
	
	public boolean equals(Object other) {
		return other instanceof PlainText && same((PlainText) other);
	}
	
	public String toString() {
		return "lit(" + spelling + ")";
	}

	private boolean same(PlainText other) {
		return spelling.equals(other.spelling);
	}
}