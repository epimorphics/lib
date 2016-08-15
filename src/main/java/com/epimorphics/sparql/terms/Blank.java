/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.terms;

import org.apache.jena.graph.NodeFactory;

import com.epimorphics.sparql.templates.Settings;

public class Blank implements TermAtomic, IsSparqler, IsExpr {

	final String id;
	
	public Blank() {
		this(NodeFactory.createBlankNode().getBlankNodeLabel());
	}
	
	public Blank(String id) {
		this.id = id;
	}
	
	public boolean equals(Object other) {
		return other instanceof Blank && same((Blank) other);
	}
	
	private boolean same(Blank other) {
		return id.equals(other.id);
	}

	@Override public void toSparql(int precedence, Settings s, StringBuilder sb) {
		sb.append("_:").append(id);
	}

	@Override public void toSparql(Settings s, StringBuilder sb) {
		sb.append("_:").append(id);		
	}
	
}