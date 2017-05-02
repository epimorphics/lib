/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2015 Epimorphics Limited
*/
package com.epimorphics.sparql.templates;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.jena.shared.PrefixMapping;
import com.epimorphics.sparql.terms.IsSparqler;

public class Settings {

	final Map<String, IsSparqler> params = new HashMap<String, IsSparqler>();
	final PrefixMapping pm = PrefixMapping.Factory.create();
	final Set<String> usedPrefixes = new HashSet<String>();
	
	public Settings() {
	}
	
	public void putParam(String name, IsSparqler ts) {
		params.put(name, ts);
	}

	public IsSparqler getParam(String name) {
		return params.get(name);
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

	/**
		Conservative pattern to check the local name part of a URI.
		Letters, digits, and underbars generally accepted. Dot(.),
		colon(:), and hyphen(-) only permitted infixed.
	*/
	static final Pattern suffixPattern = Pattern.compile("^[_A-Za-z0-9]+([-.:][_A-Za-z0-9]+)*[_A-Za-z0-9]*$");
		
	public String usePrefix(String URI) {
		
		String shortened = pm.shortForm(URI);
				
		int colonPos = shortened.indexOf(':');
		String prefix = shortened.substring(0, colonPos);
		String suffix = shortened.substring(colonPos + 1);
		
		if (!suffixPattern.matcher(suffix).find()) {
			return URI;			
		}
		
		if (!shortened.equals(URI)) {
			usedPrefixes.add(prefix);
		}
		return shortened;
	}
	
	public Set<String> getUsedPrefixes() {
		return usedPrefixes;
	}

}
