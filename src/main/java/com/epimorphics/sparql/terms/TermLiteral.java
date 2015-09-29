/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

public class TermLiteral implements TermAtomic {

	final String spelling;
	final String lang;
	final TermURI type;
	
	public TermLiteral(String spelling, TermURI type, String lang) {
		this.spelling = spelling;
		this.type = type;
		this.lang = lang;
	}

	public String getSpelling() {
		return spelling;
	}

	public String getLexicalForm() {
		return spelling;
	}

	public String getLanguage() {
		return lang;
	}

	public TermURI getLiteralType() {
		return type;
	}
	
	public String toString() {
		String basis = "'" + spelling + "'";
		return 
			basis 
			+ (lang.length() > 0 ? "@" + lang : "")
			+ (type == null ? "" : "^^" + type)
			;
	}
	
	public boolean equals(Object other) {
		return other instanceof TermLiteral && same((TermLiteral) other);
	}
	
	public int hashCode() {
		return spelling.hashCode() + type.hashCode() + lang.hashCode();
	}

	private boolean same(TermLiteral other) {
		return 
			spelling.equals(other.spelling)
			&& type.equals(other.type)
			&& lang.equals(other.lang)
			;
	}

}
