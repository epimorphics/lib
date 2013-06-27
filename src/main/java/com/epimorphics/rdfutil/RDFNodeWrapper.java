/******************************************************************
 * File:        RDFNodeWrapper.java
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

import com.epimorphics.util.EpiException;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.util.Closure;
import com.hp.hpl.jena.sparql.util.FmtUtils;

/**
 * Provides a wrapper round an RDFNode from some model in some dataset.
 * The wrapper provides uniform use of transaction/locking for safe access
 * plus automatic prefix expansion to simplify use from scripting languages.
 *
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class RDFNodeWrapper {

    protected RDFNode node;
    protected ModelWrapper modelw;

    public RDFNodeWrapper(ModelWrapper modelw, RDFNode node) {
        this.node = node;
        this.modelw = modelw;
        if (node.getModel() == null) {
            this.node = node.inModel(modelw.getModel());
        }
    }


    // Generic java object requirements

    @Override
    public boolean equals(Object other) {
        if (other instanceof RDFNodeWrapper) {
            return ((RDFNodeWrapper)other).node.equals(node);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }

    @Override
    public String toString() {
        if (node.isLiteral()){
            return node.asLiteral().toString();
        } else if (node.isURIResource()) {
            String s = modelw.shortForm( node.asResource().getURI() );
            return s.equals(":") ? node.asResource().getURI() : s;
                        // degenerate case where resource = default namespace prefix
        } else {
            return "[" + node.asResource().getId().toString() + "]";
        }
    }

    /**
     * Format a wrapped node as though it was being serialise as Turtle. Useful for embedding
     * a node into, for example, a SPARQL query.
     *
     * @return The formatted node
     */
    public String asTurtle() {
        return asTurtle(asRDFNode(), modelw.getPrefixes());
    }

    protected String asTurtle(RDFNode val, PrefixMapping prefixes) {
        if (val.isAnon()) {
            StringBuffer render = new StringBuffer();
            render.append("[");
            StmtIterator i = val.asResource().listProperties();
            while(i.hasNext()) {
                Statement s = i.nextStatement();
                render.append( asTurtle(s.getPredicate(), prefixes) );
                render.append(" ");
                render.append( asTurtle(s.getObject(), prefixes) );
                render.append("; ");
            }
            render.append("]");
            return render.toString();
        } else {
            return FmtUtils.stringForNode( val.asNode(), prefixes );
        }
    }

    public ModelWrapper getModelW() {
        return modelw;
    }

    /** Return unwrapped Jena RDFNode */
    public RDFNode asRDFNode() {
        return node;
    }

    // Literal related accessors

    /** tests true of the node is a literal */
    public boolean isLiteral() {
        return node.isLiteral();
    }

    /** Return as a Jena Literal object */
    public Literal asLiteral() {
        return node.asLiteral();
    }

    /** Return true if the node is an RDF list */
    public boolean isList() {
        if (isAnon()) {
            return node.asResource().canAs(RDFList.class);
        }
        return false;
    }

    /** Return the contents of the RDF list as a list of wrapped nodes */
    public List<RDFNodeWrapper> asList() {
        List<RDFNode> rawlist = node.as(RDFList.class).asJavaList();
        List<RDFNodeWrapper> result = new ArrayList<RDFNodeWrapper>( rawlist.size() );
        for (RDFNode n : rawlist) {
            result.add( modelw.getNode(n) );
        }
        return result;
    }

    /** Return the lexical form for a literal, the URI of a resource, the anonID of a bNode */
    public String getLexicalForm() {
        return lexicalForm(node);
    }

    private String lexicalForm(RDFNode n) {
        if (n.isLiteral()) {
            return n.asLiteral().getLexicalForm();
        } else if (n.isURIResource()) {
            return n.asResource().getURI();
        } else {
            return n.asResource().getId().toString();
        }
    }

    /** Return a name for the resource, falling back on curies or localnames if no naming propery is found */
    public String getName() {
        if (node.isLiteral()) {
            return getLexicalForm();
        } else {
            Resource r = node.asResource();
            modelw.lock();
            try {
                String label = RDFUtil.getLabel(r, modelw.getLanguage());
                if (label != null && !label.isEmpty()) {
                    return label;
                } else {
                    return r.isAnon() ? "[]" : r.getURI();
                }
            } finally {
                modelw.unlock();
            }
        }
    }


    /** If this is a literal return its language, otherwise return null */
    public String getLanguage() {
        if (node.isLiteral()) {
            return node.asLiteral().getLanguage();
        } else {
            return null;
        }
    }

    /** If this is a literal return the literal value as a java object, otherwise returns self */
    public Object getValue() {
        if (node.isLiteral()) {
            return node.asLiteral().getValue();
        }
        return this;
    }

    /** If this is a literal return its datatype as a wrapped node, otherwise return null */
    public RDFNodeWrapper getDatatype() {
        if (node.isLiteral()) {
            return modelw.getNode( node.asLiteral().getDatatypeURI() );
        } else {
            return null;
        }
    }

    // Resource related accessors

    /** Return true if this is an RDF resource */
    public boolean isResource() {
        return node.isResource();
    }

    /** Return true if this is an anonymous resource */
    public boolean isAnon() {
        return node.isAnon();
    }

    /** Return as a Jena Resource object */
    public Resource asResource() {
        return node.asResource();
    }

    /** Return the URI */
    public String getURI() {
        if (node.isResource()) {
            if (node.isAnon()) {
                // An attempt at bNode round tripping for browsing purposes only
                return "_:" + node.asResource().getId().getLabelString();
            } else {
                return node.asResource().getURI();
            }
        } else {
            return null;
        }
    }

    /** Return the shortform curi for the URI if possible, else the full URI */
    public String getShortURI() {
        if (node.isResource()) {
            if (node.isAnon()) {
                return "_:" + node.asResource().getId().getLabelString();
            } else {
                return modelw.shortForm( node.asResource().getURI() );
            }
        } else {
            return null;
        }
    }

    public Property toProperty(Object prop) {
        if (prop instanceof String) {
            return modelw.model.createProperty( modelw.expandPrefix((String)prop) );
        } else if (prop instanceof Property) {
            return (Property) prop;
        } else if (prop instanceof Resource) {
            return modelw.model.createProperty( ((Resource)prop).getURI() );
        } else if (prop instanceof RDFNodeWrapper) {
            return toProperty( ((RDFNodeWrapper)prop).node );
        } else {
            return null;
        }
    }

    /** Return a single value for the property of null if there is none, property can be specified using URI strings, curies or nodes */
    public RDFNodeWrapper get(Object prop) {
        return getPropertyValue(prop);
    }

    /** Return a single value for the property of null if there is none, property can be specified using URI strings, curies or nodes */
    public RDFNodeWrapper getPropertyValue(Object prop) {
        if (node.isResource()) {
            modelw.lock();
            try {
                Statement s =node.asResource().getProperty( toProperty(prop) );
                if (s != null) return new RDFNodeWrapper(modelw, s.getObject() );
            } finally {
                modelw.unlock();
            }
        }
        return null;
    }

    /**
     * Return a string value for the given property, selecting one in the model's locale language if possible
     */
    public String getLocalizedValue(Object prop) {
        if (node.isResource()) {
            Property p = toProperty(prop);
            modelw.lock();
            try {
                return RDFUtil.findLangMatchValue(node.asResource(), modelw.getLanguage(), p);
            } finally {
                modelw.unlock();
            }
        }
        return null;
    }

    /** Return true if the property has the given value */
    public boolean hasResourceValue(Object prop, Object value) {
        if (node.isResource()) {
            modelw.lock();
            boolean has = modelw.getModel().contains(asResource(), toProperty(prop), modelw.getResource(value));
            modelw.unlock();
            return has;
        }
        return false;
    }

    /** Return the value of the given property as a list of literal values or wrapped nodes, property can be specified using URI strings, curies or nodes */
    public List<RDFNodeWrapper> listPropertyValues(Object prop) {
        List<RDFNodeWrapper> result = new ArrayList<RDFNodeWrapper>();
        modelw.lock();
        try {
            if (node.isResource()) {
                StmtIterator si = node.asResource().listProperties( toProperty(prop) );
                while (si.hasNext()) {
                    result.add( modelw.getNode(si.next().getObject()) );
                }
            }
            return result;
        } finally {
            modelw.unlock();
        }
    }

    /**
     * Return the set of property values of this node as a ordered list of value bindings
     */
    public  List<PropertyValue> listProperties() {
        PropertyValueSet pvalues = new PropertyValueSet(modelw);
        if (node.isResource()) {
            modelw.lock();
            try {
                StmtIterator si = node.asResource().listProperties();
                while (si.hasNext()) {
                    pvalues.add( si.next() );
                }
            } finally {
                modelw.unlock();
            }
        }
        return pvalues.getOrderedValues();
    }

    /** Return list of nodes that point to us by the given property */
    public List<RDFNodeWrapper> listInLinks(Object prop) {
        List<RDFNodeWrapper> result = new ArrayList<RDFNodeWrapper>();
        modelw.lock();
        try {
            StmtIterator si = node.getModel().listStatements(null, toProperty(prop), node);
            while (si.hasNext()) {
                result.add( modelw.getNode( si.next().getSubject() ) );
            }
        } finally {
            modelw.unlock();
        }
        return result;
    }

    /** Return the set of nodes which point to us  */
    public  List<PropertyValue> listInLinks() {
        PropertyValueSet pvalues = new PropertyValueSet(modelw);
        if (node.isResource()) {
            modelw.lock();
            try {
                StmtIterator si = modelw.model.listStatements(null, null, node);
                while (si.hasNext()) {
                    Statement s = si.next();
                    pvalues.add(s.getPredicate(), s.getSubject()  );
                }
            } finally {
                modelw.unlock();
            }
        }
        return pvalues.getOrderedValues();
    }

    /**
     * Check each property in a comma-separated list of named properties and return the
     * value of the first that has a value
     */
    public RDFNodeWrapper firstValueOf(String props) {
        if (!isResource()) return null;
        Resource r = node.asResource();
        modelw.lock();
        try {
            for (String propname : props.split(",")) {
                Property p = ResourceFactory.createProperty( modelw.expandPrefix(propname) );
                if (r.hasProperty(p)) {
                    return getPropertyValue(p);
                }
            }
            return null;
        } finally {
            modelw.unlock();
        }
    }

    /**
     * Return the list nodes which link to this one via a SPARQL property path
     */
    public List<RDFNodeWrapper> connectedNodes(String path) {
        if (!node.isURIResource()) {
            throw new EpiException("Attempted to find things connected to a non (URI) resource");
        }
        String query = "SELECT DISTINCT ?i WHERE { <" + node.asResource().getURI() +"> " + path + " ?i .} ORDER BY ?i";
        ResultSetRewindable rs = modelw.querySelect(query);
        List<RDFNodeWrapper> results = new ArrayList<RDFNodeWrapper>();
        while (rs.hasNext()) {
            QuerySolution soln = rs.next();
            results.add( new RDFNodeWrapper(modelw, soln.get("i")) );
        }
        return results;
    }

    /**
     * Return a wrapped memory model containing the bNode closure of this resource
     */
    public ModelWrapper closure() {
        modelw.lock();
        try {
            Model result = ModelFactory.createDefaultModel();
            if (node.isResource()) {
                Closure.closure(node.asResource(), false, result);
            }
            return new ModelWrapper(result);
        } finally {
            modelw.unlock();
        }
    }

    /**
     * Added the bNode closure of this resource into the given (wrapped) model
     */
    public ModelWrapper closure(ModelWrapper target) {
        modelw.lock();
        target.lockWrite();
        try {
            if (node.isResource()) {
                Closure.closure(node.asResource(), false, target.getModel());
            }
            return target;
        } finally {
            modelw.unlock();
            target.unlock();
        }
    }

}
