/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.exprs;

import org.junit.Test;

import com.epimorphics.sparql.exprs.Call;
import com.epimorphics.sparql.exprs.Op;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsExpr;

import static com.epimorphics.sparql.exprs.LeafExprs.*;
import static com.epimorphics.test.utils.MakeCollection.*;
import static org.junit.Assert.*;

public class TestCall {

	@Test public void testExprPrefix() {
		Op op = new Op("sameTerm");
		IsExpr A = integer(3), B = integer(4);
		Call ep = new Call(op, A, B);
		assertSame(op, ep.getOp());
		assertEquals(list(A, B), ep.getOperands());
		
		StringBuilder sb = new StringBuilder();
		ep.toSparql(new Settings(), sb);
		assertEquals("sameTerm(3, 4)", sb.toString());
	}
}
