/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.exprs;

import com.epimorphics.sparql.terms.IsExpr;
import com.epimorphics.sparql.terms.Literal;
import com.epimorphics.sparql.terms.URI;
import com.epimorphics.sparql.terms.Var;

public class LeafExprs {

	public static IsExpr integer(int i) {
		URI type = Literal.xsdInteger;
		return new Literal("" + i, type, "");
	}
	
	public static IsExpr bool(boolean b) {
		URI type = Literal.xsdBoolean;
		return new Literal(b ? "true" : "false", type, "");
	}
	
	public static IsExpr var(String name) {
		return new Var(name);
	}
}
