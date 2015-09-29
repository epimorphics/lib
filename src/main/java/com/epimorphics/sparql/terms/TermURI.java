/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

public class TermURI implements TermAtomic {

	final String URI;
	
	public TermURI(String URI) {
		this.URI = URI;
	}

	public String getURI() {
		return URI;
	}

	public String getSpelling() {
		return URI;
	}

	public String toString() {
		return "<" + URI + ">";
	}
	
	public boolean equals(Object other) {
		return 
			other instanceof TermURI 
			&& this.URI.equals(((TermURI) other).URI)
			;
	}
	
	public int hashCode() {
		return URI.hashCode();
	}
}
