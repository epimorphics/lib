/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.RDFNode;

public class TermUtils {

	public static IsExpr nodeToTerm(RDFNode value) {
		Node n = value.asNode();
		if (n.isURI()) return new URI(n.getURI());
		if (n.isLiteral()) {
			String spelling = n.getLiteralLexicalForm();
			String typeURI = n.getLiteralDatatypeURI();
			String language = n.getLiteralLanguage();
			URI type = new URI(typeURI);
			return new Literal(spelling, type, language);
		}
		return null;
	}

}
