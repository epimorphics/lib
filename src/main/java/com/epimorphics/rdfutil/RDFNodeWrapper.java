/******************************************************************
 * File:        RDFNodeWrapper.java
 * Created by:  Dave Reynolds
 * Created on:  18 Apr 2011
 *
 * (c) Copyright 2011, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.rdfutil;

import java.util.*;

import com.epimorphics.util.EpiException;
import com.epimorphics.vocabs.SKOS;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.util.FmtUtils;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

/**
 * Provides convenient access to Jena RDFNodes for use from
 * scripting languages and templates. The underlying RDFNode
 * object is available. The wrapper provides:
 * <ul>
 *  <li>convenient access to lexical forms and names,</li>
 *  <li>use strings (curies via PrefixManager, maybe short names in the future) to define properties traverse,</li>
 *  <li>succinct sparql query,</li>
 *  <li>returning values as Lists rather than iterators for easy of scripting access concurrency management.</li>
 * </ul>
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */

// This could be implemented direct over Node instead of RDFNode, review

// TODO add support for getting language-tagged values back more easily

public class RDFNodeWrapper {

    protected RDFNode node;
    protected ModelWrapper wmodel;

    public RDFNodeWrapper(RDFNode node, ModelWrapper wmodel) {
        this.node = node;
        this.wmodel = wmodel;
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
            String s = wmodel.shorten( node.asResource().getURI() );
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
     * TODO does not handle prefix abbreviations, since <code>wmodel</code> has a {@link PrefixManager}
     * that cannot be used as a {@link PrefixMapping}
     *
     * @return The formatted node
     */
    public String asTurtle() {
        return FmtUtils.stringForNode( node.asNode() );
    }

    public ModelWrapper getModelW() {
        return wmodel;
    }

    /** Return unwrapped Jena RDFNode */
    public RDFNode asNode() {
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
        } else {
            return this;
        }
    }

    /** If this is a literal return its datatype as a wrapped node, otherwise return null */
    public RDFNodeWrapper getDatatype() {
        if (node.isLiteral()) {
            return wmodel.getNode( node.asLiteral().getDatatypeURI() );
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

    protected Property[] nameProps = new Property[]{ RDFS.label, SKOS.prefLabel, SKOS.altLabel, };

    /** Return a name for the resource, falling back on curies or localnames if no naming propery is found */
    public String getName() {
        if (node.isLiteral()) {
            return getLexicalForm();
        } else {
            Resource r = node.asResource();
            wmodel.lock();
            try {
                for (Property p : nameProps) {
                    if (r.hasProperty(p)) {
                        return lexicalForm( r.getProperty(p).getObject() );
                    }
                }
            } finally {
                wmodel.unlock();
            }
            // TODO consult vocabulary manager
            if (r.isAnon()) {
                return "[]";        // TODO review, should this be an anonID?
            } else {
                String s = wmodel.shorten(r.getURI());
                if (s.equals(r.getURI())) {
                    String ln = r.getLocalName();
                    return (ln == null || ln.length() == 0) ? r.getURI() : ln;
                } else {
                    return s;
                }
            }
        }
    }

    /**
     * Convert an object value into something useful for scripting.
     * Numbers and strings are returned as such,
     * the rest are left as wrapped nodes.
     */
    protected Object asValue(RDFNode n) {
        if (n.isLiteral()) {
            Literal l = n.asLiteral();
            if (l.getLanguage() == null || l.getLanguage().isEmpty()) {
                if (l.getDatatype() == null || l.getDatatypeURI().equals(XSD.xstring) || l.getValue() instanceof Number) {
                    return l.getValue();
                }
            }
        }
        return new RDFNodeWrapper(n, wmodel);
    }

    private Property toProperty(Object prop) {
        if (prop instanceof String) {
            return wmodel.model.createProperty( wmodel.expand((String)prop) );
        } else if (prop instanceof Property) {
            return (Property) prop;
        } else if (prop instanceof Resource) {
            return wmodel.model.createProperty( ((Resource)prop).getURI() );
        } else if (prop instanceof RDFNodeWrapper) {
            return toProperty( ((RDFNodeWrapper)prop).node );
        } else {
            return null;
        }
    }


    /** Return a single value for the property of null if there is none, property can be specified using URI strings, curies or nodes */
    public Object get(Object prop) {
        return getPropertyValue(prop);
    }

    /** Return a single value for the property of null if there is none, property can be specified using URI strings, curies or nodes */
    public Object getPropertyValue(Object prop) {
        if (node.isResource()) {
            wmodel.lock();
            Statement s =node.asResource().getProperty( toProperty(prop) );
            wmodel.unlock();
            if (s != null) return asValue( s.getObject() );
        }
        return null;
    }

    /** Return the value of the given property as a list of literal values or wrapped nodes, property can be specified using URI strings, curies or nodes */
    public List<Object> listPropertyValues(Object prop) {
        List<Object> result = new ArrayList<Object>();
        wmodel.lock();
        if (node.isResource()) {
            StmtIterator si = node.asResource().listProperties( toProperty(prop) );
            while (si.hasNext()) {
                result.add( asValue(si.next().getObject()) );
            }
        }
        wmodel.unlock();
        return result;
    }

    /** Return the set of property values of this node as a map from shortened property names to lists of values/nodes */
    public Map<String, List<Object>> listProperties() {
        Map<String, List<Object>> result = new HashMap<String, List<Object>>();
        wmodel.lock();
        if (node.isResource()) {
            StmtIterator si = node.asResource().listProperties();
            while (si.hasNext()) {
                Statement s = si.nextStatement();
                String prop = wmodel.shorten(s.getPredicate().getURI());
                List<Object> values = result.get(prop);
                if (values == null) {
                    values = new ArrayList<Object>();
                    result.put(prop, values);
                }
                values.add( asValue(s.getObject()) );
            }
        }
        wmodel.unlock();
        return result;
    }

    /** Return a node that point to us by the given property, or null if there is none */
    public Object getInLink(Object prop) {
        wmodel.lock();
        StmtIterator si = node.getModel().listStatements(null, toProperty(prop), node);
        Object result = null;
        if (si.hasNext()) {
            result = asValue( si.next().getSubject() );
        }
        wmodel.unlock();
        return result;
    }

    /** Return list of nodes that point to us by the given property */
    public List<Object> listInLinks(Object prop) {
        List<Object> result = new ArrayList<Object>();
        wmodel.lock();
        StmtIterator si = node.getModel().listStatements(null, toProperty(prop), node);
        while (si.hasNext()) {
            result.add( asValue(si.next().getSubject()) );
        }
        wmodel.unlock();
        return result;
    }

    /** Return the set of nodes which point to us as a map from property name to subject node */
    public Map<String, List<Object>> listInLinks() {
        Map<String, List<Object>> result = new HashMap<String, List<Object>>();
        wmodel.lock();
        StmtIterator si = node.getModel().listStatements(null, null, node);
        while (si.hasNext()) {
            Statement s = si.nextStatement();
            String prop = wmodel.shorten(s.getPredicate().getURI());
            List<Object> values = result.get(prop);
            if (values == null) {
                values = new ArrayList<Object>();
                result.put(prop, values);
            }
            values.add( asValue(s.getSubject()) );
        }
        wmodel.unlock();
        return result;
    }

    // SPARQL query support

    /**
     * Run a SPARQL query on the model, returning a list of maps of all result values.
     * Literal values in the result will be wrapped.
     * An initial "SELECT * WHERE" can be
     * omitted and prefix declarations will be inserted from the prefix manager so
     * a simple '{?item rdf:type eg:Example .}' is a complete legal query. The ?this
     * variable can be used to refer to this node in the query.
     */
    public List<Map<String, RDFNodeWrapper>> query(String queryS) {
        return wmodel.query(queryS, this);
    }

    /**
     * Run a SPARQL query on the model, returning a list of all
     * matches of the ?item variable.
     * Literal values in the result will be wrapped.
     * An initial "SELECT * WHERE" can be
     * omitted and prefix declarations will be inserted from the prefix manager so
     * a simple '{?item rdf:type eg:Example .}' is a complete legal query. The ?this
     * variable can be used to refer to this node in the query.
     */
    public List<RDFNodeWrapper> queryList(String queryS) {
        return wmodel.queryList(queryS, this);
    }

    /**
     * Run a SPARQL query on the model, returning a list of maps of all result values.
     * Literal values in the result will be returned as plain java objects.
     * An initial "SELECT * WHERE" can be
     * omitted and prefix declarations will be inserted from the prefix manager so
     * a simple '{?item rdf:type eg:Example .}' is a complete legal query. The ?this
     * variable can be used to refer to this node in the query.
     */
    public List<Map<String, Object>> queryValues(String queryS) {
        return wmodel.queryValues(queryS, this);
    }

    /**
     * Run a SPARQL query on the model, returning a list of all
     * Literal values in the result will be returned as plain java objects.
     * matches of the ?item variable. An initial "SELECT * WHERE" can be
     * omitted and prefix declarations will be inserted from the prefix manager so
     * a simple '{?item rdf:type eg:Example .}' is a complete legal query. The ?this
     * variable can be used to refer to this node in the query.
     */
    public List<Object> queryListValues(String queryS) {
        return wmodel.queryListValues(queryS, this);
    }

    // Support for model updating - caller should ensure a write lock surrounding whole set of operations

    /**
     * Add a (resource) value for a property. Allows URI strings and Curies to specify both property and value
     * No write locking - caller should ensure lock is taken.
     */
    public void addProperty(Object prop, Object value) {
        try {
            asResource().addProperty( wmodel.asProperty(prop), wmodel.asResource(value));
        } catch (Exception e) {
            throw new EpiException("Unable to add property value to this node: " + node);
        }
    }


    /**
     * Remove any current value for a property. Allows URI strings and Curies to specify both property and value
     * No write locking - caller should ensure lock is taken.
     */
    public void removeProperty(Object prop) {
        try {
            asResource().removeAll( wmodel.asProperty(prop) );
        } catch (Exception e) {
            throw new EpiException("Unable to add property value to this node: " + node);
        }
    }

    /**
     * Add a literal value for a property. Allows URI strings and Curies to specify both property and value
     * No write locking - caller should ensure lock is taken.
     */
    public void addLiteral(Object prop, Object value) {
        try {
            if (value instanceof String) {
                asResource().addProperty( wmodel.asProperty(prop), (String)value );
            } else if (value instanceof RDFNode) {
                asResource().addProperty( wmodel.asProperty(prop), (RDFNode)value );
            } else if (value instanceof RDFNodeWrapper) {
                asResource().addProperty( wmodel.asProperty(prop), ((RDFNodeWrapper)value).asNode() );
            } else {
                asResource().addLiteral( wmodel.asProperty(prop), value);
            }
        } catch (Exception e) {
            throw new EpiException("Unable to add property value to this node: " + node);
        }
    }

}
