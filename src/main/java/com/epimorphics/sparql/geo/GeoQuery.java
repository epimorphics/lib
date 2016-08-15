/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.geo;

import java.util.Arrays;
import java.util.List;

import com.epimorphics.sparql.query.QueryShape;
import com.epimorphics.sparql.query.Transform;
import com.epimorphics.sparql.query.Transforms;
import com.epimorphics.sparql.terms.Var;
import com.epimorphics.util.ListUtils;

public class GeoQuery {
	
	/**
	   'spatial' is the namespace of Jena Spatial properties. It is
	   copied here (rather than imported) to reduce dependencies. 
	*/
	public static final String spatial = "http://jena.apache.org/spatial#";

	/**
	    `withinBox ?id x y r` geo-query name
	*/
	public static final String withinBox = "withinBox";
	
	/**
		`withinCircle ?id x y r`
	*/
	public static final String withinCircle = "withinCircle";

	/**
	    An AbstractSParqlQuery transform that translates geo-queries
	    using Jena Spatial indexes.
	*/
	public static final Transform byIndex = new GeoTransformByJenaSpatial();
	
	/**
	    An AbstractSparqlQuery transform that translates geo-queries
	    into SPARQL filters.
	*/
	public static final Transform byFilter = new GeoTransformBySparqlFilter(); 
	
	static {
		Transforms.put(byIndex);
		Transforms.put(byFilter);
	}
	
	/**
	    A Build object knows how to apply a geo-query to an
	    AbstractSparqlQuery.
	*/
	public interface Build {
		void spatialApply(GeoQuery gq, QueryShape aq);
	}

	/**
		The list of variables bound (or checked) by this GeoQuery.
		Typically this is one element, the selected item, but it
		may be addition result properties.
	*/
	final List<Var> toBind;
	
	/**
	    The name of this GeoQuery, currently one of the `within`
	    queries.
	*/
	final String name;
	
	/**
		The numeric arguments for this GeoQuery, currently for the
		`within` queries x, y (co-ordinates of centre point) and
		r (ordinary radius or manhatten radius). 
	*/
	final List<Number> args;
	
	public GeoQuery(Var toBind, String name, Number ... args) {
		this(ListUtils.list(toBind), name, args);
	}	
	
	public GeoQuery(Var toBind, String name, List<Number> args) {
		this.toBind = ListUtils.list(toBind);
		this.name = name;
		this.args = args;
	}	
	
	public GeoQuery(List<Var> toBind, String name, Number ... args) {
		this.toBind = toBind;
		this.name = name;
		this.args = Arrays.asList(args);
	}

	@Override public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Geo{");
		sb.append(toBind);
		sb.append(" ");
		sb.append(name);
		for (Number n: args) sb.append(" ").append(n);
		sb.append(" }");
		return sb.toString();
	}
	
	public String getName() {
		return name;
	}

	public Var getVar() {
		return toBind.get(0);
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

	public List<Number> getArgs() {
		return args;
	}

}
