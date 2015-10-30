/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import com.epimorphics.sparql.templates.Settings;

public interface IsExpr extends TermAtomic {
	
	public void toSparql(int precedence, Settings s, StringBuilder sb);

}