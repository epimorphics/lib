/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.exprs;

import static com.epimorphics.sparql.exprs.LeafExprs.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.exprs.Infix;
import com.epimorphics.sparql.exprs.Op;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsExpr;

public class TestInfix {

	@Test public void testConstructExprInfix() {
		Op op = new Op("=");
		IsExpr L = integer(1), R = integer(2);
		Infix t = new Infix(L, op, R);
		
		assertSame(L, t.getL());
		assertSame(op, t.getOp());
		assertSame(R, t.getR());
		
		StringBuilder sb = new StringBuilder();
		t.toSparql(new Settings(), sb);
		assertEquals("1 = 2", sb.toString());
	}
}
