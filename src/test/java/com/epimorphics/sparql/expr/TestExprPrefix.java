/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.expr;

import org.junit.Test;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.TermExpr;

import static com.epimorphics.test.utils.MakeCollection.*;
import static org.junit.Assert.*;

import static com.epimorphics.sparql.expr.LeafExprs.*;

public class TestExprPrefix {

	@Test public void testExprPrefix() {
		Op op = new Op("sameTerm");
		TermExpr A = integer(3), B = integer(4);
		ExprPrefix ep = new ExprPrefix(op, A, B);
		assertSame(op, ep.getOp());
		assertEquals(list(A, B), ep.getOperands());
		
		StringBuilder sb = new StringBuilder();
		ep.toSparql(new Settings(), sb);
		assertEquals("sameTerm(3, 4)", sb.toString());
	}
}
