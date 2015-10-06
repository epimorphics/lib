/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/

package com.epimorphics.sparql.expr;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.terms.TermExpr;
import com.epimorphics.test.utils.SparqlUtils;

import static com.epimorphics.sparql.expr.LeafExprs.*;

public class TestPrecedence {

	static final TermExpr A = var("A");
	static final TermExpr B = var("B");
	static final TermExpr C = var("C");

	@Test public void testPrecedence() {
		int plusPrecedence = Op.opPlus.precedence;
		int minusPrecedence = Op.opMinus.precedence;
		int mulPrecedence = Op.opMul.precedence;
		assertTrue(plusPrecedence == minusPrecedence);
		assertTrue(mulPrecedence < plusPrecedence);
		
		TermExpr AplusB = new ExprInfix(A, Op.opPlus, B);
		TermExpr BmulC = new ExprInfix(B, Op.opMul, C);
		
		assertEquals("?A + ?B", SparqlUtils.renderToSparql(AplusB));
		assertEquals("?B * ?C", SparqlUtils.renderToSparql(BmulC));
		
		TermExpr AplusB_mulC = new ExprInfix(AplusB, Op.opMul, C);
		assertEquals("(?A + ?B) * ?C", SparqlUtils.renderToSparql(AplusB_mulC));
		
		TermExpr Cmul_AplusB = new ExprInfix(C, Op.opMul, AplusB);
		assertEquals("?C * (?A + ?B)", SparqlUtils.renderToSparql(Cmul_AplusB));
		
		TermExpr AplusB_plusC = new ExprInfix(AplusB, Op.opPlus, C);
		assertEquals("?A + ?B + ?C", SparqlUtils.renderToSparql(AplusB_plusC));
		
		TermExpr BplusC = new ExprInfix(B, Op.opPlus, C);
		TermExpr Aplus_BplusC = new ExprInfix(A, Op.opPlus, BplusC);
		assertEquals("?A + ?B + ?C", SparqlUtils.renderToSparql(Aplus_BplusC));
		
	}
	
}
