/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.query.QueryShape;
import com.epimorphics.sparql.terms.TermAtomic;
import com.epimorphics.sparql.terms.URI;
import com.epimorphics.sparql.terms.Var;
import com.epimorphics.sparql.text.TextQuery;

public class TestTextPatterns {

	@Test public void testText() {
		URI P = new URI("eh:/P");
		TextQuery tq = new TextQuery(new Var("v"), P, "target");
		QueryShape q = new QueryShape();
		assertNull(q.getTextQuery());
		q.setTextQuery(tq);
		assertSame(tq, q.getTextQuery());
	}
}
