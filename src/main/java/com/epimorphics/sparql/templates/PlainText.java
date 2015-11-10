/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.templates;

public class PlainText implements Element {
	
	final String text;
	
	PlainText(String text) {
		this.text = text;
	}
	
	public boolean equals(Object other) {
		return other instanceof PlainText && same((PlainText) other);
	}
	
	public String toString() {
		return "\"" + text + "\"";
	}

	private boolean same(PlainText other) {
		return text.equals(other.text);
	}

	@Override public void subst(StringBuilder sb, Settings s) {
		sb.append(text);
	}
}