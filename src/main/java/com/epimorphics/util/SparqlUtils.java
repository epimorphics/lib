/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.util;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsSparqler;

public class SparqlUtils {

	public static String renderToSparql(IsSparqler ts) {
		return renderToSparql(new Settings(), ts);
	}
	
	public static String renderToSparql(Settings s, IsSparqler ts) {
		StringBuilder sb = new StringBuilder();
		ts.toSparql(s, sb);
		return sb.toString();
	}

}
