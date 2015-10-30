/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import com.epimorphics.sparql.query.Query;
import com.epimorphics.sparql.templates.Settings;

public class Select implements PatternCommon, GraphPattern {

	final Query q;
	
	public Select(Query q) {
		this.q = q;
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append("{");
		q.toSparqlSelect(s, sb);
		sb.append("}");
	}
	
}