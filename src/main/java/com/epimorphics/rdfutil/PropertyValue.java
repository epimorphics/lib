/******************************************************************
 * File:        PropertyValues.java
 * Created by:  Dave Reynolds
 * Created on:  19 Mar 2012
 * 
 * (c) Copyright 2012, Epimorphics Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *****************************************************************/

package com.epimorphics.rdfutil;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Property;

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

    public Boolean isMultilingual() {
        return values.size() > 1 && values.stream().anyMatch( node ->
            node.getLanguage() != null && !node.getLanguage().isEmpty()
        );
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
