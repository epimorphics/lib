/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.structure;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.epimorphics.sparql.expr.LeafExprs;
import com.epimorphics.sparql.patterns.GraphPattern;
import com.epimorphics.sparql.patterns.GraphPatternBasic;
import com.epimorphics.sparql.patterns.GraphPatternBuilder;
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
		
		assertEquals(MakeCollection.list(SPA, SQB), gp.elements());
	}
	
	@Test public void testBasicTriplePatternToSparql() {
		TermTriple SPA = new TermTriple(S, P, A);
		TermFilter f = new TermFilter(new TermLiteral("17", TermLiteral.xsdInteger, ""));
		List<PatternBase> elements = new ArrayList<PatternBase>();
		elements.add(f);
		elements.add(SPA);
		GraphPatternBasic b = new GraphPatternBasic(elements);
		
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
		
		PatternBase x = new TermTriple(S, P, A);
		List<PatternBase> elements = MakeCollection.list(x);
		GraphPattern operand = new GraphPatternBasic(elements);
		GraphPatternOptional g = new GraphPatternOptional(operand);
		
		String basicResult = renderToSparql(operand);
		String optionalResult = renderToSparql(g);
		
		assertEquals("OPTIONAL {" + basicResult + "}", optionalResult);		
	}
	
	@Test public void testUnionPatternToSparql() {
		
		GraphPattern x = basics(new TermTriple(A, P, A));
		GraphPattern y = basics(new TermTriple(A, Q, B));
		
		GraphPatternUnion u = new GraphPatternUnion(x, y);
		
		String xRendering = renderToSparql(x);
		String yRendering = renderToSparql(y);
		
		String expected = "{" + xRendering + " UNION " + yRendering + "}";
		String unionResult = renderToSparql(u);
		
		assertEquals(expected, unionResult);
	}

	private GraphPattern basics(PatternBase... ps) {
		GraphPatternBuilder b = new GraphPatternBuilder();
		for (PatternBase p: ps) b.addElement(p);
		return b.build();
	}

	private String renderToSparql(TermSparql ts) {
		StringBuilder sb = new StringBuilder();
		ts.toSparql(new Settings(), sb);
		return sb.toString();
	}
}
