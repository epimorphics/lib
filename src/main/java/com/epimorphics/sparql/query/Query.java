/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.query;

import java.util.ArrayList;
import java.util.List;

import com.epimorphics.sparql.patterns.GraphPattern;
import com.epimorphics.sparql.templates.Settings;
import com.epimorphics.sparql.terms.TermProjection;
import com.epimorphics.sparql.terms.TermVar;

public class Query {
	
	protected GraphPattern where = new GraphPattern() {
		
		@Override public void toSparql(Settings s, StringBuilder sb) {
			sb.append("{}");
		}
	};
	
	protected int limit = -1;
	protected int offset = -1;
	
	final List<TermProjection> selectedVars = new ArrayList<TermProjection>();

	public String toSparql(Settings s) {
		StringBuilder sb = new StringBuilder();
		toSparql(s, sb);
		return sb.toString();
	}

	private void toSparql(Settings s, StringBuilder sb) {
		sb.append("SELECT");
		if (selectedVars.isEmpty()) {
			sb.append(" *");
		} else {
			for (TermProjection x: selectedVars) {
				sb.append(" ");
				x.toSparql(s, sb);
			}
		}
		sb.append(" WHERE ");
		where.toSparql(s, sb);
		if (limit > -1) sb.append(" LIMIT ").append(limit);
		if (offset > -1) sb.append(" OFFSET ").append(offset);
		sb.append("");
		
	}

	public void setPattern(GraphPattern where) {
		this.where = where;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void setOffset(int offset) {
		this.offset = offset;
		
	}

	public void addProjection(TermProjection x) {
		selectedVars.add(x);
	}
	
}