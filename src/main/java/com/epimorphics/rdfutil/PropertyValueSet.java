/******************************************************************
 * File:        PropertyValueSet.java
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

/**
 * Set of property/value bindings for some root resource
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class PropertyValueSet {

    protected Map<Property, PropertyValue> pvalues = new HashMap<Property, PropertyValue>();
    protected ModelWrapper modelw;
    
    public PropertyValueSet(ModelWrapper modelw) {
        this.modelw = modelw;
    }
    
    public void add(Statement s) {
        add(s.getPredicate(), s.getObject());
    }
    
    public void add(Property p, RDFNode n) {
        PropertyValue pv = pvalues.get(p);
        if (pv == null) {
            pv = new PropertyValue(modelw, p);
            pvalues.put(p, pv);
        }
        pv.addValue(new RDFNodeWrapper(modelw, n));
    }
    
    public List<PropertyValue> getValues() {
        return new ArrayList<PropertyValue>( pvalues.values() );
    }
    
    public List<PropertyValue> getOrderedValues() {
        List<PropertyValue> result = getValues();
        Collections.sort(result);
        return result;
    }
}
