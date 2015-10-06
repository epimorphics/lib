/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.expr;

import com.epimorphics.sparql.terms.TermExpr;
import com.epimorphics.sparql.terms.TermLiteral;
import com.epimorphics.sparql.terms.TermURI;
import com.epimorphics.sparql.terms.TermVar;

public class LeafExprs {

	public static TermExpr integer(int i) {
		TermURI type = TermLiteral.xsdInteger;
		return new TermLiteral("" + i, type, "");
	}
	
	public static TermExpr bool(boolean b) {
		TermURI type = TermLiteral.xsdBoolean;
		return new TermLiteral(b ? "true" : "false", type, "");
	}
	
	public static TermExpr var(String name) {
		return new TermVar(name);
	}
}
