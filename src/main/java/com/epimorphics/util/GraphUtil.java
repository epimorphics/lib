/******************************************************************
 * File:        GraphUtil.java
 * Created by:  Dave Reynolds
 * Created on:  17 Feb 2013
 * 
 * (c) Copyright 2013, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.util;

import java.util.Set;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.CollectionFactory;
import com.hp.hpl.jena.util.iterator.ClosableIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.WrappedIterator;

/**
 * Utilities for working at the Graph/Node level. Probably exist in better form 
 * somewhere in Jena.
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class GraphUtil {

    public static ExtendedIterator<Node> subjectsFor( Graph g, Node p, Node o ) { 
        Set<Node> objects = CollectionFactory.createHashedSet();
        ClosableIterator<Triple> it = g.find( Node.ANY, p, o );
        while (it.hasNext()) objects.add( it.next().getSubject() );
        return WrappedIterator.createNoRemove( objects.iterator() );
    }

}
