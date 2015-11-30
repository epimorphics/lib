/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transforms {
	
	final List<String> names = new ArrayList<String>();
	
	final Map<String, Transform> transforms = new HashMap<String, Transform>();

	public Transforms add(String name, Transform t) {
		names.add(name);
		transforms.put(name, t);
		return this;
	}
	
	public AbstractSparqlQuery apply(AbstractSparqlQuery q) {
		AbstractSparqlQuery c = q.copy();
		for (String name: names) c = transforms.get(name).apply(c);
		return c;
	}
}