/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import com.epimorphics.sparql.templates.Settings;

public class TermURI extends Spelling implements TermAtomic, TermSparql {
	
	public TermURI(String URI) {
		super(URI);
	}

	public String getURI() {
		return spelling;
	}

	public String toString() {
		return "<" + spelling + ">";
	}
	
	public boolean equals(Object other) {
		return 
			other instanceof TermURI 
			&& this.spelling.equals(((TermURI) other).spelling)
			;
	}
	
	public int hashCode() {
		return spelling.hashCode();
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		String using = s.usePrefix(spelling);
		if (using.equals(spelling)) {
			sb.append("<").append(spelling).append(">");			
		} else {
			sb.append(using);
		}
	}
}
