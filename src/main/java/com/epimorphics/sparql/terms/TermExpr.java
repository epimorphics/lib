/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import com.epimorphics.sparql.templates.Settings;

public interface TermExpr extends TermAtomic {
	
	public void toSparql(int precedence, Settings s, StringBuilder sb);

}