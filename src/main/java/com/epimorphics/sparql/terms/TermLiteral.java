/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import org.apache.jena.datatypes.xsd.XSDDatatype;

import com.epimorphics.sparql.templates.Settings;

public class TermLiteral extends Spelling implements TermAtomic, TermSparql, TermExpr {

	final String lang;
	final TermURI type;
	
	public TermLiteral(String spelling, TermURI type, String lang) {
		super(spelling);
		this.type = type;
		this.lang = lang;
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

	public static final TermURI xsdBoolean = new TermURI(XSDDatatype.XSDboolean.getURI());
	public static final TermURI xsdString = new TermURI(XSDDatatype.XSDstring.getURI());
	public static final TermURI xsdInteger = new TermURI(XSDDatatype.XSDinteger.getURI());
	public static final TermURI xsdDecimal = new TermURI(XSDDatatype.XSDdecimal.getURI());
	public static final TermURI xsdFloat = new TermURI(XSDDatatype.XSDfloat.getURI());
	public static final TermURI xsdDouble = new TermURI(XSDDatatype.XSDdouble.getURI());
	
	@Override public void toSparql(Settings s, StringBuilder sb) {
		
		if (type == null) {
			sb.append("'").append(spelling).append("'");
			if (lang.length()> 0) sb.append("@").append(lang);
		} else if (type.equals(xsdBoolean)) {
			sb.append(spelling.equals("true") || spelling.equals("1") ? "true" : "false");
		} else if (type.equals(xsdInteger)) {
			sb.append(spelling);
		} else if (type.equals(xsdString)) {
			sb.append("'").append(spelling).append("'");
		} else if (type.equals(xsdDecimal)) {
			sb.append(spelling);
		} else if (type.equals(xsdFloat)) {
			sb.append(spelling);
		} else if (type.equals(xsdDouble)) {
			sb.append(spelling);
		} else {
			sb.append("'").append(spelling).append("'");
			sb.append("^^");
			type.toSparql(s, sb);
		}	
	}
}
