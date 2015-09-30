/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.templates;

import com.epimorphics.sparql.terms.Spelling;

public class PlainText extends Spelling implements Element {
	
	PlainText(String spelling) {
		super(spelling);
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

	@Override public void subst(StringBuilder sb, Settings s) {
		sb.append(spelling);
	}
}