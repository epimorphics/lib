/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.graphpatterns;

import com.epimorphics.sparql.templates.Settings;

public class Exists extends GraphPattern  {

	final boolean exists;
	final GraphPattern P;
	
	public Exists(boolean exists, GraphPattern P) {
		this.exists = exists;
		this.P = P;
	}

	@Override public void toSparqlUnWrapped(Settings s, StringBuilder sb) {
		sb.append("IF");
		if (!exists) sb.append(" NOT");
		sb.append(" EXISTS ");
		P.toSparqlUnWrapped(s, sb);
	}

	@Override public void toSparqlWrapped(Settings s, StringBuilder sb) {
		sb.append("IF");
		if (!exists) sb.append(" NOT");
		sb.append(" EXISTS ");
		P.toSparqlUnWrapped(s, sb);
	}
	
}