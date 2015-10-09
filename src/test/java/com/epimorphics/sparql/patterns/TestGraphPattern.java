/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.epimorphics.sparql.exprs.Call;
import com.epimorphics.sparql.exprs.ExprCommon;
import com.epimorphics.sparql.exprs.Op;
import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.graphpatterns.Bind;
import com.epimorphics.sparql.graphpatterns.Builder;
import com.epimorphics.sparql.graphpatterns.GraphPattern;
import com.epimorphics.sparql.graphpatterns.Named;
import com.epimorphics.sparql.graphpatterns.Optional;
import com.epimorphics.sparql.graphpatterns.PatternCommon;
import com.epimorphics.sparql.graphpatterns.Union;
import com.epimorphics.sparql.graphpatterns.Values;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.*;

import static com.epimorphics.test.utils.SparqlUtils.*;

import static com.epimorphics.sparql.exprs.LeafExprs.*;
import static com.epimorphics.test.utils.MakeCollection.*;

public class TestGraphPattern {

	static final URI type = new URI("http://example.com/type/T");
	
	static final TermAtomic S = new URI("http://example.com/S");
	static final TermAtomic P = new URI("http://example.com/P");
	static final TermAtomic Q = new URI("http://example.com/Q");
	static final TermAtomic A = integer(17);
	static final TermAtomic B = new Literal("chat", type, "");
	
	@Test public void testBasicTriplesPattern() {
		Builder b = new Builder();
		Triple SPA = new Triple(S, P, A);
		Triple SQB = new Triple(S, Q, B);

		b.addElement(SPA);
		b.addElement(SQB);
		Basic gp = b.build();
		
		assertEquals(list(SPA, SQB), gp.getElements());
	}
	
	@Test public void testBasicTriplePatternToSparql() {
		Triple SPA = new Triple(S, P, A);
		Filter f = new Filter(new Literal("17", Literal.xsdInteger, ""));
		List<PatternCommon> elements = new ArrayList<PatternCommon>();
		elements.add(f);
		elements.add(SPA);
		Basic b = new Basic(elements);
		
		String renderF = renderToSparql(f), renderT = renderToSparql(SPA);
		String expected = "{" + renderF + " " + renderT + "}";
		StringBuilder sb = new StringBuilder();
		b.toSparql(new Settings(), sb);

		String result = sb.toString();
//		System.err.println(">> expected: " + expected);
//		System.err.println(">> obtained: " + result);	
		assertEquals(expected, result);
	}
	
	@Test public void testOptionalPatternToSparql() {
		
		PatternCommon x = new Triple(S, P, A);
		List<PatternCommon> elements = list(x);
		GraphPattern operand = new Basic(elements);
		Optional g = new Optional(operand);
		
		assertEquals(operand, g.getPattern());
		
		String basicResult = renderToSparql(operand);
		String optionalResult = renderToSparql(g);
		
		assertEquals("OPTIONAL {" + basicResult + "}", optionalResult);		
	}
	
	@Test public void testUnionPatternToSparql() {
		
		GraphPattern x = basicPattern(new Triple(A, P, A));
		GraphPattern y = basicPattern(new Triple(A, Q, B));
		
		Union u = new Union(x, y);
		
		assertEquals(list(x, y), u.getPatterns());
		
		String xRendering = renderToSparql(x);
		String yRendering = renderToSparql(y);
		
		String expected = "{" + xRendering + " UNION " + yRendering + "}";
		String unionResult = renderToSparql(u);
		
		assertEquals(expected, unionResult);
	}
	
	@Test public void testNamedGraphToSparql() {
		
		URI graph = new URI("http://example.com/graph");
		GraphPattern pattern = basicPattern(new Triple(A, P, A));
		Named n = new Named(graph, pattern);
		assertSame(graph, n.getGraphName());
		assertSame(pattern, n.getPattern());
		
		String expected = "GRAPH " + renderToSparql(graph) + " " + renderToSparql(pattern);
		String obtained = renderToSparql(n);
		assertEquals(expected, obtained);
	}
	
	@Test public void testSingleValuesPatternToSparql() {
		Var x = new Var("x");
		List<Var> vars = list(x);
		List<IsExpr> data = list(integer(1), integer(2), integer(3));
		Values v = new Values(vars, data);
		assertEquals(vars, v.getVars());
		assertEquals(data, v.getData());
		String obtained = renderToSparql(v);
		assertEquals("VALUES ?x {1 2 3}", obtained);
	}
	
	@Test public void testMultipleValuesPatternToSparql() {
		Var x = new Var("x"), y = new Var("y");
		List<Var> vars = list(x, y);
		List<IsExpr> data = list(twople(1,2), twople(3, 4));
		Values v = new Values(vars, data);
		assertEquals(vars, v.getVars());
		assertEquals(data, v.getData());
		String obtained = renderToSparql(v);
		assertEquals("VALUES (?x ?y) {(1, 2) (3, 4)}", obtained);
	}
	
	@Test public void testConstructBindPattern() {
		Var x = new Var("x");
		IsExpr e = new Var("Expression");
		Bind b = new Bind(e, x);
		
		assertSame(x, b.getVar());
		assertSame(e, b.getExpr());
		
		String obtained = renderToSparql(b);
		String expected = "BIND(?Expression AS ?x)";
		assertEquals(expected, obtained);
	}

	private IsExpr twople(int i, int j) {
		return new Call(Op.Tuple, integer(i), integer(j));
	}
	
	
	
	
	
}
