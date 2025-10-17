package com.epimorphics.webapi.dispatch;
/*
    See lda-top/LICENCE (or https://raw.github.com/epimorphics/elda/master/LICENCE)
    for the licence for this software.
    
    (c) Copyright 2011 Epimorphics Limited
    $Id$
*/

import java.util.*;

import jakarta.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
    A MatchSearcher<T> maintains a collection of MatchTemplate<T>s.
    The collection can be added to and removed from [TBD]. It can be 
    searched for an entry matching a supplied path; if there is one, 
    bindings are updated and an associated value returned.

    @author eh
*/
public class TemplateSet<T> {
    
    List<CompiledTemplate<T>> templates = new ArrayList<CompiledTemplate<T>>();
    boolean needsSorting = false;
    
    static final Logger log = LoggerFactory.getLogger( TemplateSet.class );
    
    /**
        Add the template <code>path</code> to the collection, associated
        with the supplied result value.
    */
    public void register( String path, T result ) {
    	log.debug(String.format("registering '%s' for '%s'", path, result ));
        templates.add( CompiledTemplate.prepare( path, result ) );
        needsSorting = true;
    }

    /**
        Remove the entry with the given template path from
        the collection.
    */
    public void unregister( String path ) {
    	String trimmedPath = removeQueryPart( path );
        Iterator<CompiledTemplate<T>> it = templates.iterator();
        while (it.hasNext()) {        	
            String t = it.next().template();
			if (t.equals( trimmedPath )) 
                { it.remove(); return; }
        }
    }
    
    /**
        Search the collection for the most specific entry that
        matches <code>path</code>. If there isn't one, return null.
        If there is, return the associated value, and update the
        bindings with the matches variables.
     */
    public T lookup( Map<String, String> bindings, String path, MultivaluedMap<String, String> queryParams ) {
    	if (needsSorting) sortTemplates();    
    	for (CompiledTemplate<T> t: templates) {
    		if (t.match( bindings, path, queryParams )) return t.value();
    	}
    	return null;
    }
    
    public List<String> templates() {
    	List<String> result = new ArrayList<String>();
    	if (needsSorting) sortTemplates();
    	for (CompiledTemplate<?> mt: templates) result.add(mt.template());
    	return result;
    }
    
    private String removeQueryPart( String path ) {
    	int qPos = path.indexOf('?');
		return qPos < 0 ? path : path.substring( 0, qPos );
	}

    private void sortTemplates() {
        Collections.sort( templates, CompiledTemplate.compare );
        needsSorting = false;
    }
}