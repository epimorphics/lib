/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/

package com.epimorphics.sparql.query;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsSparqler;

public class SubstSort implements IsSparqler {
	
	final Query q;
	
	SubstSort(Query q) {
		this.q = q;
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		for (OrderCondition oc: q.orderBy) oc.toSparql(s, sb);
	}
}