/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.patterns;

import static com.epimorphics.sparql.exprs.LeafExprs.integer;

import com.epimorphics.sparql.graphpatterns.GraphPattern;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.Literal;
import com.epimorphics.sparql.terms.TermAtomic;
import com.epimorphics.sparql.terms.URI;
import com.epimorphics.sparql.terms.Var;

public class SharedFixtures {

	static final URI type = new URI("http://example.com/type/T");
	
	static final TermAtomic S = new URI("http://example.com/S");
	static final TermAtomic P = new URI("http://example.com/P");
	static final TermAtomic Q = new URI("http://example.com/Q");
	
	static final TermAtomic A = integer(17);
	static final TermAtomic B = new Literal("chat", type, "");
	
	static final Var V = new Var("V");
	
	String toPatternString(GraphPattern p) {
		StringBuilder sb = new StringBuilder();
		p.toPatternString(new Settings(), sb);
		return sb.toString();
	}
	
}
