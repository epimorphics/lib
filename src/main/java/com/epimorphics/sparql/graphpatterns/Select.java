/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import com.epimorphics.sparql.query.AbstractSparqlQuery;
import com.epimorphics.sparql.templates.Settings;

public class Select extends GraphPattern {

	final AbstractSparqlQuery q;
	
	public Select(AbstractSparqlQuery q) {
		this.q = q;
	}

	@Override public void toPatternString(Settings s, StringBuilder sb) {
		q.toSparqlSelect(s, sb);
	}

	@Override protected int ordinal() {
		return Rank.Select.ordinal();
	}
	
}