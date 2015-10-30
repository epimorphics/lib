/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/

package com.epimorphics.sparql.query;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsSparqler;

public class SubstPattern implements IsSparqler {
	
	final Query q;
	
	SubstPattern(Query q) {
		this.q = q;
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		q.whereToSparql(s, sb);
	}
}