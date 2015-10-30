/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static com.epimorphics.sparql.exprs.LeafExprs.integer;
import static com.epimorphics.test.utils.MakeCollection.list;
import static com.epimorphics.util.SparqlUtils.renderToSparql;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.epimorphics.sparql.exprs.Call;
import com.epimorphics.sparql.exprs.Op;
import com.epimorphics.sparql.graphpatterns.Values;
import com.epimorphics.sparql.terms.IsExpr;
import com.epimorphics.sparql.terms.Var;

public class TestValuesPatterns extends SharedFixtures {
	
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


	private IsExpr twople(int i, int j) {
		return new Call(Op.Tuple, integer(i), integer(j));
	}
}
