/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.geo;

import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.graphpatterns.GraphPattern;
import com.epimorphics.sparql.query.QueryShape;
import com.epimorphics.sparql.query.Transform;
import com.epimorphics.sparql.terms.TermAtomic;
import com.epimorphics.sparql.terms.TermList;
import com.epimorphics.sparql.terms.Triple;
import com.epimorphics.sparql.terms.URI;
import com.epimorphics.sparql.terms.Var;

public final class TransformByIndex implements Transform {
	
	@Override public QueryShape apply(QueryShape q) {
		GeoQuery gq = q.getGeoQuery();
		if (gq == null) return q;
		return asJenaSpatial(q, gq);
	}

	private QueryShape asJenaSpatial(QueryShape q, GeoQuery gq) {
		Var S = gq.getVar();
		URI P = uriForName(gq.getName());
		TermAtomic O = TermList.fromNumbers(gq.getArgs());
		QueryShape c = q.copy();
		GraphPattern spatialPattern = new Basic(new Triple(S, P, O));
		c.addEarlyPattern(spatialPattern);
		return c;
	}

	public static URI uriForName(String name) {
		if (name.equals("withinBox")) return new URI(GeoQuery.spatial + "withinBox");
		if (name.equals("withinCircle")) return new URI(GeoQuery.spatial + "withinCircle");
		throw new RuntimeException("no URI for geo name " + name);
	}
}