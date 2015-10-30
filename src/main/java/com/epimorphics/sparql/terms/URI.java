/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import com.epimorphics.sparql.templates.Settings;

public class URI implements TermAtomic, IsExpr, IsSparqler {
	
	final String URI;
	
	public URI(String URI) {
		this.URI = URI;
	}

	public String getURI() {
		return URI;
	}

	public String toString() {
		return "<" + URI + ">";
	}
	
	public boolean equals(Object other) {
		return 
			other instanceof URI 
			&& this.URI.equals(((URI) other).URI)
			;
	}
	
	public int hashCode() {
		return URI.hashCode();
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		String using = s.usePrefix(URI);
		if (using.equals(URI)) {
			sb.append("<").append(URI).append(">");			
		} else {
			sb.append(using);
		}
	}

	@Override public void toSparql(int precedence, Settings s, StringBuilder sb) {
		toSparql(s, sb);
	}
}
