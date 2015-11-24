/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.geo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.epimorphics.sparql.query.AbstractSparqlQuery;
import com.epimorphics.sparql.terms.Var;

public class GeoQuery {

	public interface Build {
		void SpatialApply(GeoQuery gq, AbstractSparqlQuery asq);
	}
	
	public static final String withinBox = "withinBox";
	
	public static final Map<String, Build> registry = new HashMap<String, Build>();

	final Var toBind;
	final String name;
	final List<Number> args;
	
	public GeoQuery(Var toBind, String name, Number ... args) {
		this.toBind = toBind;
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
	
	public String getName() {
		return name;
	}

	public Var getVar() {
		return toBind;
	}
	
	@Override public boolean equals(Object other) {
		return other instanceof GeoQuery && same((GeoQuery) other);
	}

	private boolean same(GeoQuery other) {
		if (!toBind.equals(other.toBind)) return false;
		if (!name.equals(other.name)) return false;
		if (args.size() != other.args.size()) return false;
		for (int i = 0; i < args.size(); i += 1) 
			if (!args.get(i).equals(other.args.get(i))) return false;
		return true;
	}

	public static void register(String name, Build b) {
		registry.put(name, b);
	}
	
	public static Build lookupBuild(String name) {
		return registry.get(name);
	}

	public List<Number> getArgs() {
		return args;
	}

}
