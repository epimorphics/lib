/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import org.junit.Test;

import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.Triple;

import static com.epimorphics.test.utils.MakeCollection.*;

public class TestDescribe extends SharedFixtures {

	@Test public void testBuildDescribeNoWhere() {
		Query q = new Query();
		q.addDescribeElements(list(S, P));
		q.addDescribeElements(list(Q, V));
		String obtained = q.toSparqlDescribe(new Settings());
		String expected = 
			"DESCRIBE _S _P _Q _V"
			.replace("_S", S.toString())
			.replace("_P", P.toString())
			.replace("_Q", Q.toString())
			.replace("_V", V.toString())
			;
		assertEqualSparql(expected, obtained);
	}
	
	@Test public void testBuildDescribeWithWhere() {
		Query q = new Query();
		q.addDescribeElements(list(V));
		
		q.addEarlyPattern(new Basic(list(new Triple(S, P, V))));
		
		String obtained = q.toSparqlDescribe(new Settings());
		String expected = 
			"DESCRIBE _V WHERE {_S _P _V .}"
			.replace("_S", S.toString())
			.replace("_P", P.toString())
			.replace("_V", V.toString())
			;
		assertEqualSparql(expected, obtained);
	}
}
