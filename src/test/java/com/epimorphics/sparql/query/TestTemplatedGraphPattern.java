/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import org.junit.Test;

import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.Triple;

public class TestTemplatedGraphPattern extends SharedFixtures {

	@Test public void testGraphPatternSubstitution() {
		AbstractSparqlQuery q = new AbstractSparqlQuery();
		q.setTemplate("SELECT * WHERE $_graphPattern #END");
		q.addEarlyPattern(new Basic(new Triple(S, P, V)));
		String expected = 
			"SELECT * WHERE {_S _P _V .} #END"
			.replace("_S", S.toString())	
			.replace("_P", P.toString())	
			.replace("_V", "?V")	
			;
		String obtained = q.toSparqlSelect(new Settings());
		assertEqualSparql(expected, obtained);
	}
	
	@Test public void testSortSubstitution() {
		AbstractSparqlQuery q = new AbstractSparqlQuery();
		q.setTemplate("SELECT * WHERE {} ORDER BY$_sort #END");
		q.addOrder(Order.ASC, P);
		String expected = 
			"SELECT * WHERE {} ORDER BY ASC(_P) #END"
			.replace("_P", P.toString())	
			;
		String obtained = q.toSparqlSelect(new Settings());
		assertEqualSparql(expected, obtained);
	}
	
	@Test public void testModifierSubstitution() {
		AbstractSparqlQuery q = new AbstractSparqlQuery();
		q.setTemplate("SELECT * WHERE {} $_modifiers #END");
		q.setLimit(10);
		q.setOffset(20);
		String expected = 
			"SELECT * WHERE {}  LIMIT 10 OFFSET 20 #END"
			;
		String obtained = q.toSparqlSelect(new Settings());
		assertEqualSparql(expected, obtained);
	}
	
}
