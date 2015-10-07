/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.sparql.templates;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.shared.PrefixMapping;

import com.epimorphics.sparql.terms.IsSparqler;

public class Settings {

	final Map<String, IsSparqler> params = new HashMap<String, IsSparqler>();
	final PrefixMapping pm = PrefixMapping.Factory.create();
	
	public Settings() {
	}
	
	public void putParam(String name, IsSparqler ts) {
		params.put(name, ts);
	}

	public IsSparqler getParam(String name) {
		return params.get(name);
	}

	public void setPrefix(String name, String URI) {
		pm.setNsPrefix(name, URI);
	}

	public String usePrefix(String URI) {
		return pm.shortForm(URI);
	}

}
