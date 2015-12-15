/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.geo;

import com.epimorphics.sparql.query.QueryShape;
import com.epimorphics.sparql.query.Transform;
import com.epimorphics.sparql.terms.URI;
import com.epimorphics.sparql.terms.Var;

public final class GeoTransformBySparqlFilter implements Transform {
	
		@Override public QueryShape apply(QueryShape q) {
			GeoQuery gq = q.getGeoQuery();
			if (gq == null) return q;
			QueryShape c = q.copy();
			Var S = gq.getVar();
			URI P = GeoTransformByJenaSpatial.uriForName(gq.getName());
			// TODO
			if (true) throw new RuntimeException("TODO: geo by hand filter");
//			TermAtomic O = TermList.fromNumbers(gq.getArgs());
//			GraphPattern spatialPattern = new Basic(new Triple(S, P, O));
//			c.addEarlyPattern(spatialPattern);
			return c;
		}

		@Override public String getFullName() {
			return "GeoQuery:BySparqlFilter";
		}
	}