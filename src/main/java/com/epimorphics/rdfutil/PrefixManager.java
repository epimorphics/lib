/******************************************************************
 * File:        PrefixManager.java
 * Created by:  Dave Reynolds
 * Created on:  18 Apr 2011
 * 
 * (c) Copyright 2011, Epimorphics Limited
 * $Id:  $
 *****************************************************************/

package com.epimorphics.rdfutil;

import com.hp.hpl.jena.shared.PrefixMapping;

/**
 * Interface for a service that supports application-wide management
 * of prefix/namespace mappings. This allows scripting to make
 * stable use of curie strings and issue SPARQL queries without explicit
 * prefix declarations. The default implementation provides a 
 * builtin set of known prefixes and may call out to external prefix services
 * if required.
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 * @version $Revision: $
 */
public interface PrefixManager {

    /**
     * Registers the prefixes from the given RDF file. 
     */
    public void registerFromFile(String prefixFile) ;

    /**
     * Register a single prefix, overwriting any prior mappings for this prefix.
     */
    public void registerPrefix(String prefix, String uri);

    /**
     * Register a set of prefixes from e.g. a Model, overwriting a prior mappings
     * for the mentioned prefixes.
     */
    public void registerPrefixes( PrefixMapping mapping );
    
    /**
     * Test if the prefix of the given curie is known. 
     */
    public boolean isKnown(String curie);

    /**
     * If the curie starts with a known prefix return the expanded URI 
     * otherwise return the original string. For convenience with
     * scripting use supports both "prefix:local" and "prefix_local" notations,
     * though the latter only works for prefixes with no "_".
     */
    public String expand(String curie);

    /**
     * Convert the given uri to curie form if possible, otherwise return the uri.
     */
    public String shorten(String uri);

    /**
     * Convert the given uri to "prefix_local" form if possible, otherwise return the uri.
     */
    public String scriptShorten(String uri);
    
    /**
     * Prefix a SPARQL query by a set of SPARQL PREFIX statements for each potential
     * prefix found in the query.
     */
    public String expandQuery(String query);
    
    /**
     * Prefix a SPARQL query by a set of SPARQL PREFIX statements for each potential
     * prefix found in the query. Using both the supplied prefix mapping and the globally
     * known mappings.
     */
    public String expandQuery(String query, PrefixMapping baseMapping);
    
    /**
     * Generate a Turtle prefix for all prefixes apparently used in the given source turtle
     */
    public String prefixHeader(String turtle, PrefixMapping baseMapping);
    
    // TODO support removal of registered prefixes?
}

