/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.test.utils;

import com.epimorphics.sparql.graphpatterns.GraphPattern;
import com.epimorphics.sparql.graphpatterns.Builder;
import com.epimorphics.sparql.graphpatterns.Common;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsSparqler;

public class SparqlUtils {

	public static String renderToSparql(IsSparqler ts) {
		StringBuilder sb = new StringBuilder();
		ts.toSparql(new Settings(), sb);
		return sb.toString();
	}

	public static GraphPattern basicPattern(Common... ps) {
		Builder b = new Builder();
		for (Common p: ps) b.addElement(p);
		return b.build();
	}

}
