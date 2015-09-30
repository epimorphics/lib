/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.templates;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.shared.PrefixMapping;

import com.epimorphics.sparql.terms.TermSparql;

public class Settings {

	final Map<String, TermSparql> params = new HashMap<String, TermSparql>();
	final PrefixMapping pm = PrefixMapping.Factory.create();
	
	public Settings() {
	}
	
	public void putParam(String name, TermSparql ts) {
		params.put(name, ts);
	}

	public TermSparql getParam(String name) {
		return params.get(name);
	}

	public void setPrefix(String name, String URI) {
		pm.setNsPrefix(name, URI);
	}

	public String usePrefix(String URI) {
		return pm.shortForm(URI);
	}

}
