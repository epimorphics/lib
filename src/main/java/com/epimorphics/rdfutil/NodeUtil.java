/******************************************************************
 * File:        NodeUtil.java
 * Created by:  Dave Reynolds
 * Created on:  27 Nov 2011
 *
 * (c) Copyright 2011, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.rdfutil;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * Collection of utilities to help working with RDF at Node/Graph level.
 *
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class NodeUtil {

    /**
     * Scans the properties in props testing if root has a value for that property.
     * For the first such property it returns a list of all the lexical forms of that property.
     */
    public static List<String> literalValues(Node root, Node[]props, Graph g) {
        List<String>result = new ArrayList<String>();
        for (Node prop : props) {
            ExtendedIterator<Triple> i = g.find(root, prop, null);
            if (i.hasNext()) {
                while (i.hasNext()) {
                    Node val = i.next().getObject();
                    if (val.isLiteral()) {
                        result.add( val.getLiteralLexicalForm() );
                    }
                }
                return result;
            }
        }
        return result;
    }

    /**
     * Find the first value of the given property of the root node.
     * Return null if there's no such property.
     */
    public static Node getPropertyValue(Node root, Node prop, Graph g) {
        Node result = null;
        ExtendedIterator<Triple> i = g.find(root, prop, null);
        if (i.hasNext()) {
            result = i.next().getObject();
            i.close();
        }
        return result;
    }

    /**
     * Find the local name of the URI of a node. This is for labelling purposes
     * rather than XML serialization so splits at last # or /, may not return an NC name
     */
    public static String getLocalName(Node root) {
        if (root.isURI()) {
            String uri = root.getURI();
            int split = uri.lastIndexOf('/');
            split = Math.max(split, uri.lastIndexOf('#'));
            return uri.substring(split+1);
        }
        return null;
    }
}
