/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.util.EpiException;

public class Literal implements TermAtomic, IsSparqler, IsExpr {

	final String spelling;
	final String lang;
	final URI type;
	
	// for use by createTypedLiteral in fromNumber.
	private static final Model m = ModelFactory.createDefaultModel();
	
	public Literal(String spelling, URI type, String lang) {
		this.type = type;
		this.lang = lang;
		this.spelling = spelling;
	}
    
    public Literal(String spelling, URI type) {
        this(spelling, type, "");
    }
    
    public Literal(String spelling, String lang) {
        this(spelling, null, lang);
    }
    
    public static Literal fromRDFNode(RDFNode node) {
        if (node.isLiteral()) {
            org.apache.jena.rdf.model.Literal l = node.asLiteral();
            String uriStr = l.getDatatypeURI();
            URI uri = uriStr != null ? new URI(uriStr) : null;
            return new Literal( l.getLexicalForm(), uri, l.getLanguage() );
        } else {
            throw new EpiException("Node is not a literal: " + node);
        }
    }
	
	public static Literal fromNumber(Number n) {
		org.apache.jena.rdf.model.Literal l = m.createTypedLiteral(n);
		URI type = new URI(l.getDatatypeURI());
		return new Literal(n.toString(), type, "");
	}

	public String getLexicalForm() {
		return spelling;
	}

	public String getLanguage() {
		return lang;
	}

	public URI getLiteralType() {
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
		return other instanceof Literal && same((Literal) other);
	}
	
	public int hashCode() {
		return spelling.hashCode() + type.hashCode() + lang.hashCode();
	}

	private boolean same(Literal other) {
		return 
			spelling.equals(other.spelling)
			&& type.equals(other.type)
			&& lang.equals(other.lang)
			;
	}

	public static final URI xsdBoolean = new URI(XSDDatatype.XSDboolean.getURI());
	public static final URI xsdString = new URI(XSDDatatype.XSDstring.getURI());
	public static final URI xsdInteger = new URI(XSDDatatype.XSDinteger.getURI());
	public static final URI xsdDecimal = new URI(XSDDatatype.XSDdecimal.getURI());
	public static final URI xsdFloat = new URI(XSDDatatype.XSDfloat.getURI());
	public static final URI xsdDouble = new URI(XSDDatatype.XSDdouble.getURI());
	
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
	
	@Override public void toSparql(int precedence, Settings s, StringBuilder sb) {
		toSparql(s, sb);
	}
}
