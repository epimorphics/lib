/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/

package com.epimorphics.sparql.exprs;

import static com.epimorphics.sparql.exprs.LeafExprs.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.terms.IsExpr;
import com.epimorphics.util.SparqlUtils;

public class TestPrecedence {

	static final IsExpr A = var("A");
	static final IsExpr B = var("B");
	static final IsExpr C = var("C");

	@Test public void testPrecedence() {
		int plusPrecedence = Op.opPlus.precedence;
		int minusPrecedence = Op.opMinus.precedence;
		int mulPrecedence = Op.opMul.precedence;
		assertTrue(plusPrecedence == minusPrecedence);
		assertTrue(mulPrecedence < plusPrecedence);
		
		IsExpr AplusB = new Infix(A, Op.opPlus, B);
		IsExpr BmulC = new Infix(B, Op.opMul, C);
		
		assertEquals("?A + ?B", SparqlUtils.renderToSparql(AplusB));
		assertEquals("?B * ?C", SparqlUtils.renderToSparql(BmulC));
		
		IsExpr AplusB_mulC = new Infix(AplusB, Op.opMul, C);
		assertEquals("(?A + ?B) * ?C", SparqlUtils.renderToSparql(AplusB_mulC));
		
		IsExpr Cmul_AplusB = new Infix(C, Op.opMul, AplusB);
		assertEquals("?C * (?A + ?B)", SparqlUtils.renderToSparql(Cmul_AplusB));
		
		IsExpr AplusB_plusC = new Infix(AplusB, Op.opPlus, C);
		assertEquals("?A + ?B + ?C", SparqlUtils.renderToSparql(AplusB_plusC));
		
		IsExpr BplusC = new Infix(B, Op.opPlus, C);
		IsExpr Aplus_BplusC = new Infix(A, Op.opPlus, BplusC);
		assertEquals("?A + ?B + ?C", SparqlUtils.renderToSparql(Aplus_BplusC));
		
	}
	
}
