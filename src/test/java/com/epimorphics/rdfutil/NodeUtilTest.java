/*****************************************************************************
 * File:    NodeUtilTest.java
 * Project: epimorphics-lib
 * Created: 20 Nov 2012
 * By:      ian
 *
 * Copyright (c) 2012 Epimorphics Ltd. All rights reserved.
 *****************************************************************************/

// Package
///////////////

package com.epimorphics.rdfutil;


// Imports
///////////////

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epimorphics.util.TestUtil;
import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.graph.Node;

/**
 * <p>Unit tests for {@link NodeUtil}</p>
 *
 * @author Ian Dickinson, Epimorphics (mailto:ian@epimorphics.com)
 */
public class NodeUtilTest
{
    /***********************************/
    /* Constants                       */
    /***********************************/

    /***********************************/
    /* Static variables                */
    /***********************************/

    @SuppressWarnings( value = "unused" )
    private static final Logger log = LoggerFactory.getLogger( NodeUtilTest.class );

    /***********************************/
    /* Instance variables              */
    /***********************************/

    /***********************************/
    /* Constructors                    */
    /***********************************/

    /***********************************/
    /* External signature methods      */
    /***********************************/

    @Test
    public void testLiteralValues() {
        Model m = TestUtil.modelFixture( ":a :p1 42 . :a :p1 'foo'@en . :b :q :a. :c :p2 [:p2 0] ." );
        Graph g = m.getGraph();
        List<String> values = NodeUtil.literalValues( TestUtil.resourceFixture( m, "a" ).asNode(),
                                                      new Node[] {TestUtil.propertyFixture( m, "p1" ).asNode()},
                                                      g );
        TestUtil.testArray( values, new String[] {"42","foo"} );

        values = NodeUtil.literalValues( TestUtil.resourceFixture( m, "c" ).asNode(),
                new Node[] {TestUtil.propertyFixture( m, "p2" ).asNode()},
                g );
        TestUtil.testArray( values, new String[] {} );
    }

    @Test
    public void testGetPropertyValue() {
        Model m = TestUtil.modelFixture( ":a :p1 :d . :a :p1 :e . :b :q :a. :c :p2 [:p2 0] ." );
        Graph g = m.getGraph();

        Node n = NodeUtil.getPropertyValue( TestUtil.resourceFixture( m, "b" ).asNode(),
                                            TestUtil.propertyFixture( m, "q" ).asNode(),
                                            g );
        assertEquals( TestUtil.resourceFixture( m, "a" ).asNode(), n );

        n = NodeUtil.getPropertyValue( TestUtil.resourceFixture( m, "a" ).asNode(),
                                       TestUtil.propertyFixture( m, "p1" ).asNode(),
                g );
        assertTrue( TestUtil.resourceFixture( m, "d" ).asNode().equals( n ) ||
                    TestUtil.resourceFixture( m, "e" ).asNode().equals( n ) );
    }

    @Test
    public void testGetLocalName() {
        assertEquals( "fubar", NodeUtil.getLocalName( TestUtil.resourceFixture( null, "http://foo/bar/baz#fubar" ).asNode() ) );
        assertEquals( "fubar", NodeUtil.getLocalName( TestUtil.resourceFixture( null, "http://foo/bar/baz/fubar" ).asNode() ) );
        assertEquals( "fu.bar=99", NodeUtil.getLocalName( TestUtil.resourceFixture( null, "http://foo/bar/baz/fu.bar=99" ).asNode() ) );
        assertNull( NodeUtil.getLocalName( ResourceFactory.createPlainLiteral( "fubar" ).asNode() ));
    }

    /***********************************/
    /* Internal implementation methods */
    /***********************************/

    /***********************************/
    /* Inner class definitions         */
    /***********************************/

}

