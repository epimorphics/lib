/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import org.junit.Test;

import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.Triple;

public class TestTemplatedGraphPattern extends SharedFixtures {

	@Test public void testGraphPatternSubstitution() {
		Query q = new Query();
		q.setTemplate("SELECT * WHERE $_graphPattern #END");
		q.addPattern(new Basic(new Triple(S, P, V)));
		String expected = 
			"SELECT * WHERE {_S _P _V .} #END"
			.replace("_S", S.toString())	
			.replace("_P", P.toString())	
			.replace("_V", "?V")	
			;
		String obtained = q.toSparqlSelect(new Settings());
		assertEqualSparql(expected, obtained);
	}
	
}
