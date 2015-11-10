/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsSparqler;

public abstract class GraphPattern implements IsSparqler {

	public void toSparql(Settings s, StringBuilder sb) {
		toSparqlWrapped(s, sb);
	}
	
	public abstract void toSparqlWrapped(Settings s, StringBuilder sb);
	
	public void toSparqlUnWrapped(Settings s, StringBuilder sb) {
		sb.append("{");
		toSparqlWrapped(s, sb);
		sb.append("}");
	}
}
