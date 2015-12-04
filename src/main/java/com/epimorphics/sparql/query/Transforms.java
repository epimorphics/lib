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

	public Transforms add(Transform t) {
		String name = t.getTypeName();
		if (transforms.get(name) == null) names.add(name);
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
		return instance.getTransform(name);
	}
	
	public Transform getTransform(String name) {
		return transforms.get(name);
	}

	public static void put(Transform t) {
		instance.add(t);
	}
}