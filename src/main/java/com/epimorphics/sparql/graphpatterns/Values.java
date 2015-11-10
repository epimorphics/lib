/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/

package com.epimorphics.sparql.graphpatterns;

import java.util.ArrayList;
import java.util.List;

import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.IsExpr;
import com.epimorphics.sparql.terms.Var;

public class Values extends GraphPattern  {

	final List<Var> vars;
	final List<IsExpr> data;
	
	public Values(List<Var> vars, List<IsExpr> data) {
		this.vars = new ArrayList<Var>(vars);
		this.data = new ArrayList<IsExpr>(data);
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append("VALUES" );
		if (vars.size() > 1) {
			sb.append(" ");
			sb.append("(");
			String before = "";
			for (Var v: vars) {
				sb.append(before);
				before = " ";
				v.toSparql(s, sb);
			}
			sb.append(")");
		} else {
			sb.append(" ");
			vars.get(0).toSparql(s, sb);
		}
		sb.append(" ");
		sb.append("{");
		String before = "";
		for (IsExpr e: data) {
			sb.append(before);
			before = " ";
			e.toSparql(s, sb);
		}
		sb.append("}");
	}

	public List<Var> getVars() {
		return vars;
	}

	public List<IsExpr> getData() {
		return data;
	}

}
