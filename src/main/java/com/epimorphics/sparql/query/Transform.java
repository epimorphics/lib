/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

public interface Transform {
	public QueryShape apply(QueryShape q);
	
	public String getFullName();
}