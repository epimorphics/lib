/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.geo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Property;

public class GeoQuery {

	public static final String withinBox = "withinBox";
	
	public static final Map<String, Property> registry = new HashMap<String, Property>();

	final String name;
	final List<Number> args;
	
	public GeoQuery(String name, Number ... args) {
		this.name = name;
		this.args = Arrays.asList(args);
	}

	@Override public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Geo{");
		sb.append(name);
		for (Number n: args) sb.append(" ").append(n);
		sb.append(" }");
		return sb.toString();
		
	}
	
	@Override public boolean equals(Object other) {
		return other instanceof GeoQuery && same((GeoQuery) other);
	}

	private boolean same(GeoQuery other) {
		if (!name.equals(other.name)) return false;
		if (args.size() != other.args.size()) return false;
		for (int i = 0; i < args.size(); i += 1) 
			if (!args.get(i).equals(other.args.get(i))) return false;
		return true;
	}

	public static void register(String name, Property p) {
		registry.put(name, p);
	}
	
	public static Property getRegisteredProperty(String name) {
		return registry.get(name);
	}

}
