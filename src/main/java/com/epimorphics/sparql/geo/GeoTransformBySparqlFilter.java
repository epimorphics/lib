/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.geo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    static final Logger log = LoggerFactory.getLogger( GeoTransformBySparqlFilter.class );
    
	static final String AFN = "http://jena.hpl.hp.com/ARQ/function#";
	
	static final Op sqrt = new Op(OpType.FUNC, "<" + AFN + "sqrt" + ">");

	protected URI eastingProp = new URI("http://data.ordnancesurvey.co.uk/ontology/spatialrelations/easting");
	
	protected URI northingProp = new URI("http://data.ordnancesurvey.co.uk/ontology/spatialrelations/northing");

	protected Var eastingVar = new Var("easting");
	
	protected Var northingVar = new Var("northing");
	
	protected Var distanceVar = new Var("distance");

	public void setDistanceVar(String name) {
		distanceVar = new Var(name);
	}
	
	public void setEastingVar(String name) {
		eastingVar = new Var(name);
	}
	
	public void setNorthingVar(String name) {
		northingVar = new Var(name);
	}
	
	public void setEastingProp(String uri) {
		eastingProp = new URI(uri);
	}
	
	public void setNorthingProp(String uri) {
		northingProp = new URI(uri);
	}
	
	@Override public QueryShape apply(QueryShape q) {
		
		GeoQuery gq = q.getGeoQuery();
		if (gq == null) return q;
		if (name(gq).equals("withinCircle")) return withinCircle(q, gq);
		if (name(gq).equals("withinBox")) return withinBox(q, gq);
		log.warn("unrecognised geoquery name: " + name(gq));
		return q;
	}

	private String name(GeoQuery gq) {
		return gq.getName();
	}

	private QueryShape withinBox(QueryShape q, GeoQuery gq) {
		// x - r <= easting <= x + r && y - r <= northing <= y + r
		
		QueryShape c = q.copy();
		
		Var id = gq.getVar();
		Number x = gq.args.get(0), y = gq.args.get(1), r = gq.args.get(2);
		
		c.addLaterPattern(new Basic(new Triple(id, eastingProp, eastingVar)));
		c.addLaterPattern(new Basic(new Triple(id, northingProp, northingVar)));
		
		Literal xl = Literal.fromNumber(x);
		Literal yl = Literal.fromNumber(y);
		Literal radius = Literal.fromNumber(r);		
		
		IsExpr xMinusR = new Infix(xl, Op.opMinus, radius);
		IsExpr xPlusR = new Infix(xl, Op.opPlus, radius);
		IsExpr yMinusR = new Infix(yl, Op.opMinus, radius);
		IsExpr yPlusR = new Infix(yl, Op.opPlus, radius);
		
		IsExpr el = new Infix(xMinusR, Op.opLessEq, eastingVar);
		IsExpr er = new Infix(eastingVar, Op.opLessEq, xPlusR);

		IsExpr nl = new Infix(yMinusR, Op.opLessEq, northingVar);
		IsExpr nr = new Infix(northingVar, Op.opLessEq, yPlusR);
		
		c.addLaterPattern(new Basic(new Filter(new Infix(el, Op.opAnd, er))));
		c.addLaterPattern(new Basic(new Filter(new Infix(nl, Op.opAnd, nr))));
		
		return c;
	}

	private QueryShape withinCircle(QueryShape q, GeoQuery gq) {
		QueryShape c = q.copy();
		
		Var id = gq.getVar();
		Number x = gq.args.get(0), y = gq.args.get(1), r = gq.args.get(2);
		
		c.addLaterPattern(new Basic(new Triple(id, eastingProp, eastingVar)));
		c.addLaterPattern(new Basic(new Triple(id, northingProp, northingVar)));
		
		Literal xl = Literal.fromNumber(x);
		Literal yl = Literal.fromNumber(y);
		
		IsExpr xdiff = new Infix(eastingVar, Op.opMinus, xl);
		IsExpr ydiff = new Infix(northingVar, Op.opMinus, yl);
		
		IsExpr xl2 = new Infix(xdiff, Op.opMul, xdiff);
		IsExpr yl2 = new Infix(ydiff, Op.opMul, ydiff);
		
		IsExpr sum = new Infix(xl2, Op.opPlus, yl2);
		IsExpr root = new Call(sqrt, sum);
		
		Bind b = new Bind(root, distanceVar);
		c.addLaterPattern(b);
		
		Literal radius = Literal.fromNumber(r);
		c.addLaterPattern(new Basic(new Filter(new Infix(distanceVar, Op.opLessEq, radius))));
		
		return c;
	}

	@Override public String getFullName() {
		return "GeoQuery:BySparqlFilter";
	}
}