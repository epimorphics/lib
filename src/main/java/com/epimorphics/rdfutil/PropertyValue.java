/******************************************************************
 * File:        PropertyValues.java
 * Created by:  Dave Reynolds
 * Created on:  19 Mar 2012
 * 
 * (c) Copyright 2012, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.rdfutil;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Property;

/**
 * Property/value pair for wrapped RDF resources.
 * Sortable by local name of property.
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class PropertyValue implements Comparable<PropertyValue> {

    protected RDFNodeWrapper prop;
    protected List<RDFNodeWrapper> values;
    
    public PropertyValue(RDFNodeWrapper prop) {
        this.prop = prop;
        values = new ArrayList<RDFNodeWrapper>();
    }
    
    public PropertyValue(ModelWrapper modelw, Property prop) {
        this( new RDFNodeWrapper(modelw, prop) );
    }
    
    public PropertyValue(RDFNodeWrapper prop, RDFNodeWrapper value) {
        this.prop = prop;
        values = new ArrayList<RDFNodeWrapper>(1);
        values.add(value);
    }

    public List<RDFNodeWrapper> getValues() {
        return values;
    }

    public void addValue(RDFNodeWrapper value) {
        this.values.add( value );
    }

    public RDFNodeWrapper getProp() {
        return prop;
    }

    @Override
    public int compareTo(PropertyValue o) {
        return prop.asResource().getLocalName().compareTo( o.prop.asResource().getLocalName() );
    }
    
}
