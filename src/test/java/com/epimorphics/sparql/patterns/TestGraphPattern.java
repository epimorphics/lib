/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.epimorphics.sparql.expr.ExprPrefix;
import com.epimorphics.sparql.expr.Op;
import com.epimorphics.sparql.patterns.GraphPattern;
import com.epimorphics.sparql.patterns.GraphPatternBasic;
import com.epimorphics.sparql.patterns.GraphPatternBuilder;
import com.epimorphics.sparql.patterns.GraphPatternNamed;
import com.epimorphics.sparql.patterns.GraphPatternOptional;
import com.epimorphics.sparql.patterns.GraphPatternUnion;
import com.epimorphics.sparql.patterns.PatternBase;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.TermAtomic;
import com.epimorphics.sparql.terms.TermExpr;
import com.epimorphics.sparql.terms.TermFilter;
import com.epimorphics.sparql.terms.TermLiteral;
import com.epimorphics.sparql.terms.TermSparql;
import com.epimorphics.sparql.terms.TermTriple;
import com.epimorphics.sparql.terms.TermURI;
import com.epimorphics.sparql.terms.TermVar;
import com.epimorphics.test.utils.SparqlUtils;

import static com.epimorphics.test.utils.MakeCollection.*;
import static com.epimorphics.sparql.expr.LeafExprs.*;

public class TestGraphPattern {

	static final TermURI type = new TermURI("http://example.com/type/T");
	
	static final TermSparql S = new TermURI("http://example.com/S");
	static final TermSparql P = new TermURI("http://example.com/P");
	static final TermSparql Q = new TermURI("http://example.com/Q");
	static final TermSparql A = integer(17);
	static final TermSparql B = new TermLiteral("chat", type, "");
	
	@Test public void testBasicTriplesPattern() {
		GraphPatternBuilder b = new GraphPatternBuilder();
		TermTriple SPA = new TermTriple(S, P, A);
		TermTriple SQB = new TermTriple(S, Q, B);

		b.addElement(SPA);
		b.addElement(SQB);
		GraphPatternBasic gp = b.build();
		
		assertEquals(list(SPA, SQB), gp.getElements());
	}
	
	@Test public void testBasicTriplePatternToSparql() {
		TermTriple SPA = new TermTriple(S, P, A);
		TermFilter f = new TermFilter(new TermLiteral("17", TermLiteral.xsdInteger, ""));
		List<PatternBase> elements = new ArrayList<PatternBase>();
		elements.add(f);
		elements.add(SPA);
		GraphPatternBasic b = new GraphPatternBasic(elements);
		
		String renderF = SparqlUtils.renderToSparql(f), renderT = SparqlUtils.renderToSparql(SPA);
		String expected = "{" + renderF + " " + renderT + "}";
		StringBuilder sb = new StringBuilder();
		b.toSparql(new Settings(), sb);

		String result = sb.toString();
//		System.err.println(">> expected: " + expected);
//		System.err.println(">> obtained: " + result);	
		assertEquals(expected, result);
	}
	
	@Test public void testOptionalPatternToSparql() {
		
		PatternBase x = new TermTriple(S, P, A);
		List<PatternBase> elements = list(x);
		GraphPattern operand = new GraphPatternBasic(elements);
		GraphPatternOptional g = new GraphPatternOptional(operand);
		
		assertEquals(operand, g.getPattern());
		
		String basicResult = SparqlUtils.renderToSparql(operand);
		String optionalResult = SparqlUtils.renderToSparql(g);
		
		assertEquals("OPTIONAL {" + basicResult + "}", optionalResult);		
	}
	
	@Test public void testUnionPatternToSparql() {
		
		GraphPattern x = SparqlUtils.basicPattern(new TermTriple(A, P, A));
		GraphPattern y = SparqlUtils.basicPattern(new TermTriple(A, Q, B));
		
		GraphPatternUnion u = new GraphPatternUnion(x, y);
		
		assertEquals(list(x, y), u.getPatterns());
		
		String xRendering = SparqlUtils.renderToSparql(x);
		String yRendering = SparqlUtils.renderToSparql(y);
		
		String expected = "{" + xRendering + " UNION " + yRendering + "}";
		String unionResult = SparqlUtils.renderToSparql(u);
		
		assertEquals(expected, unionResult);
	}
	
	@Test public void testNamedGraphToSparql() {
		
		TermURI graph = new TermURI("http://example.com/graph");
		GraphPattern pattern = SparqlUtils.basicPattern(new TermTriple(A, P, A));
		GraphPatternNamed n = new GraphPatternNamed(graph, pattern);
		assertSame(graph, n.getGraphName());
		assertSame(pattern, n.getPattern());
		
		String expected = "GRAPH " + SparqlUtils.renderToSparql(graph) + " " + SparqlUtils.renderToSparql(pattern);
		String obtained = SparqlUtils.renderToSparql(n);
		assertEquals(expected, obtained);
	}
	
	@Test public void testSingleValuesPatternToSparql() {
		TermVar x = new TermVar("x");
		List<TermVar> vars = list(x);
		List<TermExpr> data = list(integer(1), integer(2), integer(3));
		GraphPatternValues v = new GraphPatternValues(vars, data);
		assertEquals(vars, v.getVars());
		assertEquals(data, v.getData());
		String obtained = SparqlUtils.renderToSparql(v);
		assertEquals("VALUES ?x {1 2 3}", obtained);
	}
	
	@Test public void testMultipleValuesPatternToSparql() {
		TermVar x = new TermVar("x"), y = new TermVar("y");
		List<TermVar> vars = list(x, y);
		List<TermExpr> data = list(twople(1,2), twople(3, 4));
		GraphPatternValues v = new GraphPatternValues(vars, data);
		assertEquals(vars, v.getVars());
		assertEquals(data, v.getData());
		String obtained = SparqlUtils.renderToSparql(v);
		assertEquals("VALUES (?x ?y) {(1, 2) (3, 4)}", obtained);
	}

	private TermExpr twople(int i, int j) {
		return new ExprPrefix(Op.Tuple, integer(i), integer(j));
	}
	
	
	
	
	
}
