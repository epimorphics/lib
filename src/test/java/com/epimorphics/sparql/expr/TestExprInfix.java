/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.expr;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.ExprInfix;
import com.epimorphics.sparql.terms.Op;
import com.epimorphics.sparql.terms.TermExpr;
import com.epimorphics.sparql.terms.TermLiteral;
import com.epimorphics.sparql.terms.TermURI;

public class TestExprInfix {

	@Test public void testConstructExprInfix() {
		Op op = new Op("=");
		TermExpr L = integer(1), R = integer(2);
		ExprInfix t = new ExprInfix(L, op, R);
		
		assertSame(L, t.getL());
		assertSame(op, t.getOp());
		assertSame(R, t.getR());
		
		StringBuilder sb = new StringBuilder();
		t.toSparql(new Settings(), sb);
		assertEquals("1 = 2", sb.toString());
	}

	private TermExpr integer(int i) {
		TermURI type = TermLiteral.xsdInteger;
		return new TermLiteral("" + i, type, "");
	}
}
