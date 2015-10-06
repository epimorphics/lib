/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/

package com.epimorphics.sparql.patterns;

import java.util.ArrayList;
import java.util.List;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.TermExpr;
import com.epimorphics.sparql.terms.TermVar;

public class GraphPatternValues implements GraphPattern  {

	final List<TermVar> vars;
	final List<TermExpr> data;
	
	public GraphPatternValues(List<TermVar> vars, List<TermExpr> data) {
		this.vars = new ArrayList<TermVar>(vars);
		this.data = new ArrayList<TermExpr>(data);
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append("VALUES" );
		if (vars.size() > 1) {
			
		} else {
			sb.append(" ");
			vars.get(0).toSparql(s, sb);
			
		}
		sb.append(" ");
		sb.append("{");
		String before = "";
		for (TermExpr e: data) {
			sb.append(before);
			before = " ";
			e.toSparql(s, sb);
		}
		sb.append("}");
	}

	public List<TermVar> getVars() {
		return vars;
	}

	public List<TermExpr> getData() {
		return data;
	}

}
