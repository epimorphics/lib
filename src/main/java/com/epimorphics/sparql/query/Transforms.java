/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transforms {

	static final Logger log = LoggerFactory.getLogger( Transforms.class );

	final List<String> names = new ArrayList<String>();
	
	final Map<String, Transform> transforms = new HashMap<String, Transform>();

	final Map<String, String> nameForType = new HashMap<String, String>();
	
	public Transforms() {
	}
		
	public Transforms add(Transform t) {
		
		String fullName = t.getFullName();
		String typeName = typeNameOf(fullName);
		
		String currentName = nameForType.get(typeName);
		
		if (currentName == null) {
			names.add(fullName);
		} else {
			for (int i = 0; i < names.size(); i += 1) {
				String name = names.get(i);
				String ty = typeNameOf(name);
				if (ty.equals(typeName)) {
					names.set(i, fullName);
					break;
				}
			}
		}
		
		nameForType.put(typeName, fullName);
		transforms.put(fullName, t);
		
		return this;
	}
	
	private String typeNameOf(String x) {
		return x.substring(0, x.indexOf(':'));
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
		log.debug("putting transform " + t.getFullName());
		instance.add(t);
	}

	public void addAll(Transforms others) {
		for (Transform t: others.transforms.values())
			add(t);
	}
}