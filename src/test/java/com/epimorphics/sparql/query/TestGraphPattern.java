/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.epimorphics.sparql.expr.LeafExprs;
import com.epimorphics.sparql.patterns.GraphPattern;
import com.epimorphics.sparql.patterns.GraphPatternBasic;
import com.epimorphics.sparql.patterns.GraphPatternBuilder;
import com.epimorphics.sparql.patterns.GraphPatternNamed;
import com.epimorphics.sparql.patterns.GraphPatternOptional;
import com.epimorphics.sparql.patterns.GraphPatternUnion;
import com.epimorphics.sparql.patterns.PatternBase;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.TermFilter;
import com.epimorphics.sparql.terms.TermLiteral;
import com.epimorphics.sparql.terms.TermSparql;
import com.epimorphics.sparql.terms.TermTriple;
import com.epimorphics.sparql.terms.TermURI;
import com.epimorphics.test.utils.MakeCollection;
import com.epimorphics.test.utils.SparqlUtils;

public class TestGraphPattern {

	static final TermURI type = new TermURI("http://example.com/type/T");
	
	static final TermSparql S = new TermURI("http://example.com/S");
	static final TermSparql P = new TermURI("http://example.com/P");
	static final TermSparql Q = new TermURI("http://example.com/Q");
	static final TermSparql A = LeafExprs.integer(17);
	static final TermSparql B = new TermLiteral("chat", type, "");
	
	@Test public void testBasicTriplesPattern() {
		GraphPatternBuilder b = new GraphPatternBuilder();
		TermTriple SPA = new TermTriple(S, P, A);
		TermTriple SQB = new TermTriple(S, Q, B);

		b.addElement(SPA);
		b.addElement(SQB);
		GraphPatternBasic gp = b.build();
		
		assertEquals(MakeCollection.list(SPA, SQB), gp.getElements());
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
		List<PatternBase> elements = MakeCollection.list(x);
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
		
		assertEquals(MakeCollection.list(x, y), u.getPatterns());
		
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
}
