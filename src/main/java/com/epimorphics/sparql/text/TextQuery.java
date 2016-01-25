/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.text;

import com.epimorphics.sparql.terms.URI;
import com.epimorphics.sparql.terms.Var;

public class TextQuery {

	final Var var;
	final URI property;
	final String target;

	public TextQuery(Var var, String target) {
		this(var, null, target);
	}
	
	public TextQuery(Var var, URI p, String target) {
		this.var = var;
		this.property = p;
		this.target = target;
	}
	
}