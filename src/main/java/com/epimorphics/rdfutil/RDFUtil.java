/******************************************************************
 * File:        RDFUtil.java
 * Created by:  Dave Reynolds
 * Created on:  11 Dec 2011
 *
 * (c) Copyright 2011, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.rdfutil;

import com.epimorphics.vocabs.SKOS;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Misc. collection of Model/Resouce level utilities
 *
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class RDFUtil {
    public static final Property[] labelProps = { SKOS.prefLabel, SKOS.altLabel, RDFS.label, DCTerms.title };
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
     * Return a label for the given resource
     */
    public static String getLabel(Resource root) {
        RDFNode label = getAPropertyValue(root, labelProps);
        if (label != null && label.isLiteral()) {
            return label.asLiteral().getLexicalForm();
        } else {
            return  NodeUtil.getLocalName(root.asNode());
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
     * Answer the integer value of property p on resource x, or
     * ifAbsent if there isn't one.
     */
    public static int getIntValue(Resource x, Property p, int ifAbsent) {
            Statement s = x.getProperty( p );
            return s == null ? ifAbsent : s.getInt();
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

}
