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
	
	public QueryShape apply(QueryShape q) {
		QueryShape c = q.copy();
		for (String name: names) c = transforms.get(name).apply(c);
		return c;
	}
	
	public static Transforms instance = new Transforms();
	
	public static Transform get(String name) {
		return instance.get(name);
	}
	
	public static void put(String name, Transform t) {
		instance.add(name, t);
	}
}