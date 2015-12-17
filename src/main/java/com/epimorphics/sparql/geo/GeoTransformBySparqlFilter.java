/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.geo;

import com.epimorphics.sparql.exprs.Call;
import com.epimorphics.sparql.exprs.Infix;
import com.epimorphics.sparql.exprs.Op;
import com.epimorphics.sparql.exprs.Op.OpType;
import com.epimorphics.sparql.graphpatterns.Basic;
import com.epimorphics.sparql.graphpatterns.Bind;
import com.epimorphics.sparql.query.QueryShape;
import com.epimorphics.sparql.query.Transform;
import com.epimorphics.sparql.terms.Filter;
import com.epimorphics.sparql.terms.IsExpr;
import com.epimorphics.sparql.terms.Literal;
import com.epimorphics.sparql.terms.Triple;
import com.epimorphics.sparql.terms.URI;
import com.epimorphics.sparql.terms.Var;

public final class GeoTransformBySparqlFilter implements Transform {

		static final String AFN = "http://jena.hpl.hp.com/ARQ/function#";
		
		static final Op sqrt = new Op(OpType.FUNC, AFN + "sqrt");

		@Override public QueryShape apply(QueryShape q) {
			
			GeoQuery gq = q.getGeoQuery();
			if (gq == null) return q;
			QueryShape c = q.copy();
			Var S = gq.getVar();
			URI P = GeoTransformByJenaSpatial.uriForName(gq.getName());
			Number x = gq.args.get(0), y = gq.args.get(1), r = gq.args.get(2);
			
			Var easting = new Var("easting");
			Var northing = new Var("northing");
			URI eastingProp = new URI("http://data.ordnancesurvey.co.uk/ontology/spatialrelations/easting");
			URI northingProp = new URI("http://data.ordnancesurvey.co.uk/ontology/spatialrelations/northing");
			
			Var id = new Var("id");
			
			c.addEarlyPattern(new Basic(new Triple(id, eastingProp, easting)));
			c.addEarlyPattern(new Basic(new Triple(id, northingProp, northing)));
			Literal xl = Literal.fromNumber(x);
			Literal yl = Literal.fromNumber(y);
			
			IsExpr xdiff = new Infix(xl, Op.opMinus, easting);
			IsExpr ydiff = new Infix(yl, Op.opMinus, northing);
			
			IsExpr xl2 = new Infix(xdiff, Op.opMul, xdiff);
			IsExpr yl2 = new Infix(ydiff, Op.opMul, ydiff);
			
			IsExpr sum = new Infix(xl2, Op.opPlus, yl2);
			IsExpr root = new Call(sqrt, sum);
			
			Var distance = new Var("distance");
			Bind b = new Bind(root, distance);
			c.addPreBinding(b);
			
			Literal radius = Literal.fromNumber(r);
			c.addEarlyPattern(new Basic(new Filter(new Infix(distance, Op.opLessEq, radius))));
			
			return c;
		}

		@Override public String getFullName() {
			return "GeoQuery:BySparqlFilter";
		}
	}