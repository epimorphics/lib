/*****************************************************************************
 * File:    RichResourcesListTest.java
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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epimorphics.util.TestUtil;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

/**
 * <p>TODO class comment</p>
 *
 * @author Ian Dickinson, Epimorphics (mailto:ian@epimorphics.com)
 */
public class RichResourcesListTest
{
    /***********************************/
    /* Constants                       */
    /***********************************/

    /***********************************/
    /* Static variables                */
    /***********************************/

    @SuppressWarnings( value = "unused" )
    private static final Logger log = LoggerFactory.getLogger( RichResourcesListTest.class );

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
    public void testRichResourcesList() {
        assertNotNull( new RichResourcesList() );
    }

    @Test
    public void testRichResourcesListModel() {
        assertNotNull( new RichResourcesList( ModelFactory.createDefaultModel() ));
    }

    @Test
    public void testAddResource() {
        RichResourcesList rrl = new RichResourcesList();
        Model m1 = TestUtil.modelFixture( ":a a :b, :c." );
        Model m2 = TestUtil.modelFixture( ":b a :f. :a a :g." );
        Resource ra = m1.getResource( TestUtil.baseURIFixture() + "a" );
        Resource rb = m2.getResource( TestUtil.baseURIFixture() + "b" );

        rrl.add( ra );
        assertTrue( rrl.get( 0 ).hasProperty( RDF.type, r( "b" ) ) );
        assertTrue( rrl.get( 0 ).hasProperty( RDF.type, r( "c" ) ) );
        assertFalse( rrl.get( 0 ).hasProperty( RDF.type, r( "g" ) ) );

        rrl.add( rb );
        assertTrue( rrl.get( 0 ).hasProperty( RDF.type, r( "b" ) ) );
        assertTrue( rrl.get( 0 ).hasProperty( RDF.type, r( "c" ) ) );
        assertTrue( rrl.get( 0 ).hasProperty( RDF.type, r( "g" ) ) );

        assertTrue( rrl.get( 1 ).hasProperty( RDF.type, r( "f" ) ) );
        assertFalse( rrl.get( 1 ).hasProperty( RDF.type, r( "g" ) ) );
    }

    @Test
    public void testAddResourceModel() {
        RichResourcesList rrl = new RichResourcesList();
        Model m1 = TestUtil.modelFixture( ":a a :b, :c." );
        Model m2 = TestUtil.modelFixture( ":b a :f. :a a :g." );
        Resource ra = ResourceFactory.createResource( TestUtil.baseURIFixture() + "a" );
        Resource rb = ResourceFactory.createResource( TestUtil.baseURIFixture() + "b" );

        rrl.add( ra, m1 );
        assertTrue( rrl.get( 0 ).hasProperty( RDF.type, r( "b" ) ) );
        assertTrue( rrl.get( 0 ).hasProperty( RDF.type, r( "c" ) ) );
        assertFalse( rrl.get( 0 ).hasProperty( RDF.type, r( "g" ) ) );

        rrl.add( rb, m2 );
        assertTrue( rrl.get( 0 ).hasProperty( RDF.type, r( "b" ) ) );
        assertTrue( rrl.get( 0 ).hasProperty( RDF.type, r( "c" ) ) );
        assertTrue( rrl.get( 0 ).hasProperty( RDF.type, r( "g" ) ) );

        assertTrue( rrl.get( 1 ).hasProperty( RDF.type, r( "f" ) ) );
        assertFalse( rrl.get( 1 ).hasProperty( RDF.type, r( "g" ) ) );
    }

    @Test
    public void testAddDescription() {
        RichResourcesList rrl = new RichResourcesList();
        Model m1 = TestUtil.modelFixture( ":a a :b, :c." );
        Model m2 = TestUtil.modelFixture( ":b a :f. :a a :g." );
        Resource ra = ResourceFactory.createResource( TestUtil.baseURIFixture() + "a" );

        rrl.add( ra, m1 );
        assertTrue( rrl.get( 0 ).hasProperty( RDF.type, r( "b" ) ) );
        assertTrue( rrl.get( 0 ).hasProperty( RDF.type, r( "c" ) ) );
        assertFalse( rrl.get( 0 ).hasProperty( RDF.type, r( "g" ) ) );

        rrl.addDescription( m2 );
        assertTrue( rrl.get( 0 ).hasProperty( RDF.type, r( "b" ) ) );
        assertTrue( rrl.get( 0 ).hasProperty( RDF.type, r( "c" ) ) );
        assertTrue( rrl.get( 0 ).hasProperty( RDF.type, r( "g" ) ) );
    }

    @Test
    public void testDescription() {
        RichResourcesList rrl = new RichResourcesList();
        Model m1 = TestUtil.modelFixture( ":a a :b, :c." );
        Model m2 = TestUtil.modelFixture( ":b a :f. :a a :g." );
        Resource ra = m1.getResource( TestUtil.baseURIFixture() + "a" );
        Resource rb = m2.getResource( TestUtil.baseURIFixture() + "b" );

        rrl.add( ra );
        assertEquals( 2, rrl.description().size() );
        rrl.add( rb );
        assertEquals( 4, rrl.description().size() );
    }

    /***********************************/
    /* Internal implementation methods */
    /***********************************/

    private Resource r( String local ) {
        return ResourceFactory.createResource( TestUtil.baseURIFixture() + local );
    }

    /***********************************/
    /* Inner class definitions         */
    /***********************************/

}

