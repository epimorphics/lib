/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.epimorphics.sparql.graphpatterns.GraphPattern;
import com.epimorphics.sparql.exprs.Call;
import com.epimorphics.sparql.exprs.Op;
import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.graphpatterns.Builder;
import com.epimorphics.sparql.graphpatterns.Named;
import com.epimorphics.sparql.graphpatterns.Optional;
import com.epimorphics.sparql.graphpatterns.Union;
import com.epimorphics.sparql.graphpatterns.Values;
import com.epimorphics.sparql.graphpatterns.Common;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.TermAtomic;
import com.epimorphics.sparql.terms.IsExpr;
import com.epimorphics.sparql.terms.Filter;
import com.epimorphics.sparql.terms.Literal;
import com.epimorphics.sparql.terms.IsSparqler;
import com.epimorphics.sparql.terms.Triple;
import com.epimorphics.sparql.terms.URI;
import com.epimorphics.sparql.terms.Var;
import com.epimorphics.test.utils.SparqlUtils;

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
		List<Common> elements = new ArrayList<Common>();
		elements.add(f);
		elements.add(SPA);
		Basic b = new Basic(elements);
		
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
		
		Common x = new Triple(S, P, A);
		List<Common> elements = list(x);
		GraphPattern operand = new Basic(elements);
		Optional g = new Optional(operand);
		
		assertEquals(operand, g.getPattern());
		
		String basicResult = SparqlUtils.renderToSparql(operand);
		String optionalResult = SparqlUtils.renderToSparql(g);
		
		assertEquals("OPTIONAL {" + basicResult + "}", optionalResult);		
	}
	
	@Test public void testUnionPatternToSparql() {
		
		GraphPattern x = SparqlUtils.basicPattern(new Triple(A, P, A));
		GraphPattern y = SparqlUtils.basicPattern(new Triple(A, Q, B));
		
		Union u = new Union(x, y);
		
		assertEquals(list(x, y), u.getPatterns());
		
		String xRendering = SparqlUtils.renderToSparql(x);
		String yRendering = SparqlUtils.renderToSparql(y);
		
		String expected = "{" + xRendering + " UNION " + yRendering + "}";
		String unionResult = SparqlUtils.renderToSparql(u);
		
		assertEquals(expected, unionResult);
	}
	
	@Test public void testNamedGraphToSparql() {
		
		URI graph = new URI("http://example.com/graph");
		GraphPattern pattern = SparqlUtils.basicPattern(new Triple(A, P, A));
		Named n = new Named(graph, pattern);
		assertSame(graph, n.getGraphName());
		assertSame(pattern, n.getPattern());
		
		String expected = "GRAPH " + SparqlUtils.renderToSparql(graph) + " " + SparqlUtils.renderToSparql(pattern);
		String obtained = SparqlUtils.renderToSparql(n);
		assertEquals(expected, obtained);
	}
	
	@Test public void testSingleValuesPatternToSparql() {
		Var x = new Var("x");
		List<Var> vars = list(x);
		List<IsExpr> data = list(integer(1), integer(2), integer(3));
		Values v = new Values(vars, data);
		assertEquals(vars, v.getVars());
		assertEquals(data, v.getData());
		String obtained = SparqlUtils.renderToSparql(v);
		assertEquals("VALUES ?x {1 2 3}", obtained);
	}
	
	@Test public void testMultipleValuesPatternToSparql() {
		Var x = new Var("x"), y = new Var("y");
		List<Var> vars = list(x, y);
		List<IsExpr> data = list(twople(1,2), twople(3, 4));
		Values v = new Values(vars, data);
		assertEquals(vars, v.getVars());
		assertEquals(data, v.getData());
		String obtained = SparqlUtils.renderToSparql(v);
		assertEquals("VALUES (?x ?y) {(1, 2) (3, 4)}", obtained);
	}

	private IsExpr twople(int i, int j) {
		return new Call(Op.Tuple, integer(i), integer(j));
	}
	
	
	
	
	
}
