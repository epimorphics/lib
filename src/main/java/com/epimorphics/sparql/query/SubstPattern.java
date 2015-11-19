/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/

package com.epimorphics.sparql.query;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsSparqler;

public class SubstPattern implements IsSparqler {
	
	final AbstractSparqlQuery q;
	
	SubstPattern(AbstractSparqlQuery q) {
		this.q = q;
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		q.whereToSparql(s, sb);
	}
}