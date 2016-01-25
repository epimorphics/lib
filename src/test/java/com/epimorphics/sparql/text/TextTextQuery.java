/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.text;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.sparql.terms.URI;
import com.epimorphics.sparql.terms.Var;

public class TextTextQuery {

	@Test public void testUsualConstructor() {
		Var v = new Var("v");
		URI property = new URI("eh:/P");
		String target = "seek and ye shall find";
		TextQuery tq = new TextQuery(v, property, target);
		
		assertSame(v, tq.var);
		assertSame(property, tq.property);
		assertSame(target, tq.target);
	}
	
	@Test public void testConstructorWithoutProperty() {
		Var v = new Var("v");
		String target = "seek and ye shall find";
		TextQuery tq = new TextQuery(v, target);
		
		assertSame(v, tq.var);
		assertSame(null, tq.property);
		assertSame(target, tq.target);
	}
	
	
}
