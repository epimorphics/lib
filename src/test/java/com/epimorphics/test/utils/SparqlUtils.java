/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.test.utils;

import com.epimorphics.sparql.patterns.GraphPattern;
import com.epimorphics.sparql.patterns.GraphPatternBuilder;
import com.epimorphics.sparql.patterns.PatternBase;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.TermSparql;

public class SparqlUtils {

	public static String renderToSparql(TermSparql ts) {
		StringBuilder sb = new StringBuilder();
		ts.toSparql(new Settings(), sb);
		return sb.toString();
	}

	public static GraphPattern basicPattern(PatternBase... ps) {
		GraphPatternBuilder b = new GraphPatternBuilder();
		for (PatternBase p: ps) b.addElement(p);
		return b.build();
	}

}
