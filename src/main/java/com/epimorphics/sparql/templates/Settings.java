/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.templates;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.shared.PrefixMapping;

import com.epimorphics.sparql.geo.GeoQuery;
import com.epimorphics.sparql.terms.IsSparqler;

public class Settings {

	final Map<String, IsSparqler> params = new HashMap<String, IsSparqler>();
	final PrefixMapping pm = PrefixMapping.Factory.create();
	final Set<String> usedPrefixes = new HashSet<String>();
	final GeoQuery.Registry registry = new GeoQuery.Registry();
	
	public Settings() {
	}
	
	public void putParam(String name, IsSparqler ts) {
		params.put(name, ts);
	}

	public IsSparqler getParam(String name) {
		return params.get(name);
	}
	
	public Settings register(String name, GeoQuery.Build build) {
		registry.register(name, build);
		return this;
	}
	
	public GeoQuery.Build lookup(String name) {
		return registry.lookup(name);
	}

	public Settings setPrefix(String name, String URI) {
		pm.setNsPrefix(name, URI);
		return this;
	}

    public Settings setPrefixMapping(PrefixMapping prefixes) {
        pm.setNsPrefixes(prefixes);
		return this;
    }

    public PrefixMapping getPrefixMapping() {
        return pm;
    }
	
	public  Map<String, String> getPrefixes() {
		return pm.getNsPrefixMap();
	}

	public String usePrefix(String URI) {
		String shortened = pm.shortForm(URI);
		if (!shortened.equals(URI)) {
			String prefix = shortened.substring(0, shortened.indexOf(':'));
			usedPrefixes.add(prefix);
		}
		return shortened;
	}
	
	public Set<String> getUsedPrefixes() {
		return usedPrefixes;
	}

}
