/******************************************************************
 * File:        RDFUtil.java
 * Created by:  Dave Reynolds
 * Created on:  11 Dec 2011
 *
 * (c) Copyright 2011, Epimorphics Limited
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
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.epimorphics.util.EpiException;
import com.epimorphics.vocabs.SKOS;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.LiteralImpl;
import org.apache.jena.sparql.util.FmtUtils;
import org.apache.jena.sparql.util.NodeFactoryExtra;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;

/**
 * Misc. collection of Model/Resouce level utilities
 *
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class RDFUtil {
    public static final Property[] labelProps = { SKOS.prefLabel, SKOS.altLabel, RDFS.label, DCTerms.title, FOAF.name, FOAF.nick };
    public static final Property[] descriptionProps = { DCTerms.description, SKOS.definition, RDFS.comment };

    /**
     * Return the first available value for the given list of properties
     */
    // TODO extend to do language filtering?
    public static RDFNode getAPropertyValue(Resource root, Property[] props) {
        for (Property p : props) {
            Statement s = root.getProperty(p);
            if (s != null) return s.getObject();
        }
        return null;
    }

    /**
     * Checks the list of properties for one for which we can return a string 
     * which either matches the given language or a plain-literal
     */
    public static String findLangMatchValue(Resource root, String language, Property...props) {
        String label = null;
        for (Property p : props) {
            for (StmtIterator si = root.listProperties(p); si.hasNext(); ) {
                RDFNode val = si.next().getObject();
                if (val.isLiteral()) {
                    Literal l = val.asLiteral();
                    if (label == null) {
                        // Keep first match as fall back
                        label = l.getLexicalForm();
                    }
                    String lang = l.getLanguage();
                    if (lang == null || lang.equals(language) || lang.isEmpty()) {
                        return l.getLexicalForm();
                    }
                }
            }
        }
        return label;
    }
    
    /**
     * Return a label for the given resource
     */
    public static String getLabel(Resource root) {
        RDFNode label = getAPropertyValue(root, labelProps);
        if (label != null && label.isLiteral()) {
            return label.asLiteral().getLexicalForm();
        } else {
            return getLocalname(root); 
        }
    }
    
    /**
     * Return a label for the given resource
     */
    public static String getLabel(Resource root, String language) {
        String label = findLangMatchValue(root, language, labelProps);
        if (label != null) {
            return label;
        } else {
            return getLocalname(root); 
        }
    }

    /**
     * Return a label for the given node
     */
    public static String getLabel(RDFNode node) {
        if (node.isLiteral()) {
            return node.asLiteral().getLexicalForm();
        } else {
            return getLabel(node.asResource());
        }
    }

    /**
     * Return a description for the given resource
     */
    public static String getDescription(Resource root) {
        RDFNode label = getAPropertyValue(root, descriptionProps);
        if (label != null && label.isLiteral()) {
            return label.asLiteral().getLexicalForm();
        } else {
            return  "";
        }
    }

    /**
     * Return a description for the given resource
     */
    public static String getDescription(Resource root, String lang) {
        String label = findLangMatchValue(root, lang, descriptionProps);
        if (label != null) {
            return label;
        } else {
            return  "";
        }
    }

    /**
     * Find the singleton subject of the given property/value pair.
     * Return null if there is no match. Return an arbitrary match if there are multiples
     */
    public static Resource getSubject(Model model, Property prop, RDFNode object) {
        ResIterator ri = model.listSubjectsWithProperty(prop, object);
        try {
            if (ri.hasNext()) {
                return ri.next();
            } else {
                return null;
            }
        } finally {
            ri.close();
        }
    }

    /**
     * Return one of the values of the property on the resource in string form.
     * If there are no values return the defaultValue. If the value is not
     * a String but is a literal return it's lexical form. If it is a resource
     * return it's URI.
     */
    public static String getStringValue(Resource r, Property p, String defaultValue) {
        Statement s = r.getProperty(p);
        if (s == null) {
            return defaultValue;
        } else {
            return getLexicalForm( s.getObject() );
        }
    }

    /**
     * Return one of the values of the property on the resource in string form.
     * If there are no values return null.
     */
    public static String getStringValue(Resource r, Property p) {
        return getStringValue(r, p, null);
    }

    /**
     * Return the lexical form of a node. This is the lexical form of a
     * literal, the URI of a URIResource or the annonID of a bNode.
     */
    public static String getLexicalForm(RDFNode value) {
        if (value.isLiteral()) {
            return ((Literal)value).getLexicalForm();
        } else if (value.isURIResource()) {
            return ((Resource)value).getURI();
        } else {
            return value.toString();
        }
    }

    /**
     * Return the value of a resource on a property as a resource, or
     * null if there isn't a resource value.
     */
    public static Resource getResourceValue(Resource subject, Property prop) {
        StmtIterator ni = subject.listProperties(prop);
        while (ni.hasNext()) {
            RDFNode n = ni.next().getObject();
            if (n instanceof Resource) {
                ni.close();
                return (Resource)n;
            }
        }
        return null;
    }

    /**
     * Return all the resource values of the property on a resource.
     */
    public static List<Resource> getResourceValues(Resource subject, Property prop) {
        List<Resource> values = new ArrayList<Resource>();
        StmtIterator ni = subject.listProperties(prop);
        while (ni.hasNext()) {
            RDFNode n = ni.next().getObject();
            if (n instanceof Resource) {
                values.add(n.asResource());
            }
        }
        return values;
    }

    /**
     * Answer the integer value of property p on resource x, or
     * ifAbsent if there isn't one.
     */
    public static int getIntValue(Resource x, Property p, int ifAbsent) {
            Statement s = x.getProperty( p );
            return s == null ? ifAbsent : s.getInt();
    }

    /**
     * Answer the long value of property p on resource x or null if there isn't one.
     */
    public static Long getLongValue(Resource x, Property p) {
        Number value = getNumericValue(x, p);
        return value == null ? null : value.longValue();
    }

    /**
     * Answer the double value of property p on resource x or null if there isn't one.
     */
    public static Double getDoubleValue(Resource x, Property p) {
        Number value = getNumericValue(x, p);
        return value == null ? null : value.doubleValue();
    }

    public static Number getNumericValue(Resource x, Property p) {
        Statement s = x.getProperty( p );
        if (s == null) {
            return null;
        } else {
            RDFNode value = s.getObject();
            if (value.isLiteral()) {
                Object valueO = value.asLiteral().getValue();
                if (valueO instanceof Number) {
                    return (Number)valueO;
                }
            }
        }
        return null;
    }

    /**
        Answer the boolean value of property <code>p</code> on resource
        <code>r</code>. If there is no p-value, or the p-value is not a
        literal, return <code>ifAbsent</code>. Otherwise return true if
        the literal has spelling "true" or "yes", false if it has the
        spelling "false" or "no", and an unspecified value otherwise.
    */
    public static boolean getBooleanValue( Resource r, Property p, boolean ifAbsent ) {
            Statement s = r.getProperty( p );
            if (s == null) return ifAbsent;
            RDFNode o = s.getObject();
            if (o.isLiteral()) {
                    Literal ol = (Literal) o;
                    String sp = ol.getLexicalForm();
                    return sp.equalsIgnoreCase("yes") || sp.equalsIgnoreCase("true");
            }
            return ifAbsent;
    }

    /**
     * Return a useful version of the local name for a resource. Not restricted to an NCName
     */
    public static String getLocalname(String uri) {
        Matcher match = lnmatch.matcher(uri);
        if (match.matches()) {
            return match.group(2);
        } else {
            return uri;
        }
    }

    /**
     * Return a useful version of the local name for a resource. Not restricted to an NCName
     */
    public static String getLocalname(Resource r) {
        if (! r.isURIResource()) {
            return null;
        }
        Matcher match = lnmatch.matcher(r.getURI());
        if (match.matches()) {
            return match.group(2);
        } else {
            return r.getLocalName();
        }
    }

    /**
     * Return a useful version of the namespace resource. Not restricted to treating the localname as an NCName
     */
    public static String getNamespace(Resource r) {
        if (! r.isURIResource()) {
            return null;
        }
        Matcher match = lnmatch.matcher(r.getURI());
        if (match.matches()) {
            return match.group(1);
        } else {
            return r.getNameSpace();
        }
    }

    static final Pattern lnmatch = Pattern.compile("(.*[#/])([^#/]+)$");


    /**
     * Convert an arbitrary value to a RDF node.
     * <ul>
     * <li>An existing node is returned unchanged</li>
     * <li>A string beginning <code>http:</code> is converted to a resource</li>
     * <li>A string ending with <code>@lang</code> becomes a lang-tagged literal</li>
     * <li>Other strings become plain literals</li>
     * <li>All other non-null values are passed to the literal factory</li>
     * <li>Null becomes a new bnode</li>
     * </ul>
     *
     * @param val Any value
     * @return val converted to an {@link RDFNode}
     */
    public static RDFNode asRDFNode( Object val ) {
        if (val instanceof RDFNode) {
            return (RDFNode) val;
        }
        else if (val instanceof String) {
            String s = (String) val;
            if (s.startsWith( "http:" ) || s.startsWith("https:")) {
                return ResourceFactory.createResource( s );
            } else {
                Matcher match = langPattern.matcher(s);
                if (match.matches()) {
                    return ResourceFactory.createLangLiteral(match.group(1), match.group(2));
                } else {
                    return ResourceFactory.createPlainLiteral( s );
                }
            }
        }
        else if (val != null) {
            return ResourceFactory.createTypedLiteral( val );
        }
        else {
            return ResourceFactory.createResource();
        }
    }
    
    static Pattern langPattern = Pattern.compile("^(.*)@([-a-zA-Z]*)$");

    /**
     * Timestamp a resource using current time
     */
    public static void timestamp(Resource resource, Property prop) {
        resource.addProperty(prop, resource.getModel().createTypedLiteral(Calendar.getInstance()));
    }

    /**
     * Copy all values of the given property from the source to the dest resource
     */
    public static void copyProperty(Resource src, Resource dest, Property p) {
        copyProperty(src, dest, p, p);
    }

    /**
     * Copy all values of p from src to values of newp on dest.
     */
    public static void copyProperty(Resource src, Resource dest, Property p, Property newp) {
        StmtIterator si = src.listProperties(p);
        while (si.hasNext()){
            Statement s = si.next();
            dest.addProperty(newp, s.getObject());
        }
    }

    /**
     * Create a datetime literal representing the given Date (in unix ms since epoch style)
     */
    public static Literal fromDateTime(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        Object value = new XSDDateTime(cal);
        return ResourceFactory.createTypedLiteral(value);
    }

    /**
     * Convert a datetime literal to a unix style date stamp
     */
    public static long asTimestamp(RDFNode n) {
        if (n.isLiteral()) {
            Literal l = n.asLiteral();
            if (l.getDatatype().equals(XSDDatatype.XSDdateTime)) {
                return ((XSDDateTime)l.getValue()).asCalendar().getTimeInMillis();
            }
        }
        throw new EpiException("Node is not a datetime literal: " + n);
    }

    /**
     * Return the root resource of a model (or the first root we find if there are multiple).
     * Return null there is no root.
     */
    public static Resource findRoot(Model m) {
        for (ResIterator ri = m.listSubjects(); ri.hasNext();) {
            Resource root = ri.next();
            if (m.listStatements(null, null, root).hasNext()) {
                continue;
            } else {
                return root;
            }
        }
        return null;
    }

    /**
     * Return all the root resources of a model 
     */
    public static List<Resource> findRoots(Model m) {
        List<Resource> roots = new ArrayList<Resource>();
        for (ResIterator ri = m.listSubjects(); ri.hasNext();) {
            Resource root = ri.next();
            if (m.listStatements(null, null, root).hasNext()) {
                continue;
            } else {
                roots.add(root);
            }
        }
        return roots;
    }


    /**
     * Return a collection of all the distinct properties of the given resource
     */
    public static Set<Property> allPropertiesOf(Resource r) {
        Set<Property> result = new HashSet<Property>();
        for (StmtIterator si = r.listProperties(); si.hasNext();) {
            result.add(si.next().getPredicate());
        }
        return result;
    }

    /**
     * Create a property with the same URI as the given resource
     */
    public static Property asProperty(Resource r) {
        if (r == null || r.isAnon()) return null;
        return ResourceFactory.createProperty(r.getURI());
    }

    /**
     * Return a list of all the resource-values of the property on the resource
     */
    public static List<Resource> allResourceValues(Resource resource, Property p) {
        List<Resource> results = new ArrayList<Resource>();
        for (StmtIterator si = resource.listProperties(p); si.hasNext(); ) {
            RDFNode o = si.next().getObject();
            if (o.isResource()) {
                results.add( o.asResource() );
            }
        }
        return results;
    }

    /**
     * Serialize a (URI or literal) RDFNode to a string which can be later parsed
     */
    public static String serlialize(RDFNode node) {
        return FmtUtils.stringForNode(node.asNode());
    }

    /**
     * Decode a serlialized RDFNode, not associated with any useful model
     */
    public static RDFNode deserialize(String ser) {
        Node n = NodeFactoryExtra.parseNode(ser);
        if (n.isLiteral()) {
            return new LiteralImpl(n, null);
        } else if (n.isURI()) {
            return ResourceFactory.createResource(n.getURI());
        } else {
            return ResourceFactory.createResource();
        }
    }
    
    /**
     * Map a namespace used in a model
     */
    public static Model mapNamespace(Model model, String originalNS, String replacementNS) {
        Model result = ModelFactory.createDefaultModel();
        result.setNsPrefixes(model);
        for (StmtIterator si = model.listStatements(); si.hasNext();) {
            Statement s = si.next();
            Resource subject = (Resource) mapNamespace(result, s.getSubject(), originalNS, replacementNS);
            Property predicate = (Property) mapNamespace(result, s.getPredicate(), originalNS, replacementNS);
            RDFNode  object = mapNamespace(result, s.getObject(), originalNS, replacementNS);
            result.add(subject, predicate, object);
        }
        return result;
    }
    
    private static RDFNode mapNamespace(Model resultModel, RDFNode node, String originalNS, String replacementNS) {
        if (node.isURIResource()) {
            String uri = node.asResource().getURI();
            if (uri.startsWith(originalNS)) {
                String newuri = uri.replace(originalNS, replacementNS);
                if (node instanceof Property) {
                    return resultModel.createProperty(newuri);
                } else {
                    return resultModel.createResource(newuri);                    
                }
            }
        }
        return node.inModel(resultModel);
    }
}
