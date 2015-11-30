/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.geo;

import java.util.Arrays;
import java.util.List;

import com.epimorphics.sparql.graphpatterns.*;
import com.epimorphics.sparql.query.AbstractSparqlQuery;
import com.epimorphics.sparql.query.Transform;
import com.epimorphics.sparql.terms.TermAtomic;
import com.epimorphics.sparql.terms.TermList;
import com.epimorphics.sparql.terms.Triple;
import com.epimorphics.sparql.terms.URI;
import com.epimorphics.sparql.terms.Var;
import com.epimorphics.util.ListUtils;

public class GeoQuery {

	public interface Build {
		void SpatialApply(GeoQuery gq, AbstractSparqlQuery asq);
	}
	
	public static final String spatial = "http://jena.apache.org/spatial#";
	
	/**
		A transform that rewrites geo queries as jena spatial queries.
		Only does withinBox and withinCircle.
	*/
	public static final Transform byIndex = new Transform() {

		@Override public AbstractSparqlQuery apply(AbstractSparqlQuery q) {
			GeoQuery gq = q.getGeoQuery();
			if (gq == null) return q;
			AbstractSparqlQuery c = q.copy();
			Var S = gq.getVar();
			URI P = uriForName(gq.getName());
			TermAtomic O = TermList.fromNumbers(gq.getArgs());
			GraphPattern spatialPattern = new Basic(new Triple(S, P, O));
			c.addEarlyPattern(spatialPattern);
			return c;
		}

		private URI uriForName(String name) {
			if (name.equals("withinBox")) return new URI(spatial + "withinBox");
			if (name.equals("withinCircle")) return new URI(spatial + "withinCircle");
			throw new RuntimeException("no URI for geo name " + name);
		}
	};

	private static URI uriForName(String name) {
		if (name.equals("withinBox")) return new URI(spatial + "withinBox");
		if (name.equals("withinCircle")) return new URI(spatial + "withinCircle");
		throw new RuntimeException("no URI for geo name " + name);
	}
	
	public static final Transform byFilter = new Transform() {

		@Override public AbstractSparqlQuery apply(AbstractSparqlQuery q) {
			GeoQuery gq = q.getGeoQuery();
			if (gq == null) return q;
			AbstractSparqlQuery c = q.copy();
			Var S = gq.getVar();
			URI P = uriForName(gq.getName());
			// TODO
			if (true) throw new RuntimeException("TODO: geo by hand filter");
//			TermAtomic O = TermList.fromNumbers(gq.getArgs());
//			GraphPattern spatialPattern = new Basic(new Triple(S, P, O));
//			c.addEarlyPattern(spatialPattern);
			return c;
		}}; 
	
	
	public static final String withinBox = "withinBox";

	final List<Var> toBind;
	final String name;
	final List<Number> args;
	
	public GeoQuery(Var toBind, String name, Number ... args) {
		this(ListUtils.list(toBind), name, args);
	}	
	
	public GeoQuery(List<Var> toBind, String name, Number ... args) {
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
