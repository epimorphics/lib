/*****************************************************************************
 * File:    QueryUtilTest.java
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

import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epimorphics.util.PrefixUtils;
import com.epimorphics.util.TestUtil;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.*;

/**
 * <p>TODO class comment</p>
 *
 * @author Ian Dickinson, Epimorphics (mailto:ian@epimorphics.com)
 */
public class QueryUtilTest
{
    /***********************************/
    /* Constants                       */
    /***********************************/

    /***********************************/
    /* Static variables                */
    /***********************************/

    @SuppressWarnings( value = "unused" )
    private static final Logger log = LoggerFactory.getLogger( QueryUtilTest.class );

    @Test
    public void testSubstitue() {
        String query = "SELECT * WHERE {?i ${0} ${1}}";
        String result = QueryUtil.substituteInQuery(query, "rdf:type", "foaf:Person");
        assertEquals("SELECT * WHERE {?i rdf:type foaf:Person}", result);
    }

    @Test @Ignore
    public void testAddPageLimits() {
        fail( "Not yet implemented" );
    }

    @Test @Ignore
    public void testResultsFor() {
        fail( "Not yet implemented" );
    }

    @Test
    public void testCreateBindings() {
        Resource r = TestUtil.resourceFixture( null, "r" );
        QuerySolutionMap qsm = QueryUtil.createBindings( "a", r.getURI(), "b", 42 );
        assertNotNull( qsm );
        assertEquals( r, qsm.get( "a" ) );
        assertTrue( qsm.get("b").isLiteral() );
        assertEquals( 42, qsm.get( "b" ).asLiteral().getInt() );
    }

    @Test
    public void testSelectAll1() {
        Model m = TestUtil.modelFixture( ":a :p :b ; :p :c. :e :p :g." );
        ResultSet rs = QueryUtil.selectAll( m, "select ?item {?a :p ?item}" ) ;

        Set<RDFNode> seen = new HashSet<RDFNode>();
        while (rs.hasNext()) {
            seen.add( rs.next().get( "item" ) );
        }

        TestUtil.testArray( seen.iterator(), new RDFNode[] {TestUtil.resourceFixture( null, "b" ),
                                                            TestUtil.resourceFixture( null, "c" ),
                                                            TestUtil.resourceFixture( null, "g" )} );
    }

    @Test
    public void testSelectAll2() {
        Model m = TestUtil.modelFixture( ":a :p :b ; :p :c. :e :p :g." );

        // bind ?a to :e
        ResultSet rs = QueryUtil.selectAll( m, "select ?item {?a :p ?item}", null, "a", TestUtil.resourceFixture( m, "e" ) ) ;

        Set<RDFNode> seen = new HashSet<RDFNode>();
        while (rs.hasNext()) {
            seen.add( rs.next().get( "item" ) );
        }

        TestUtil.testArray( seen.iterator(), new RDFNode[] {TestUtil.resourceFixture( null, "g" )} );
    }

    @Test
    public void testSelectAll3() {
        Model m = TestUtil.modelFixture( "@prefix foo: <http://fu.bar/test#>.\n :a :p :b ; :p :c. :e :p foo:g." );
        ResultSet rs = QueryUtil.selectAll( m, "select ?item {?a :p ?item}", PrefixUtils.asPrefixes( "foo", "http://fu.bar/test#", "", TestUtil.baseURIFixture() ) ) ;

        Set<RDFNode> seen = new HashSet<RDFNode>();
        while (rs.hasNext()) {
            seen.add( rs.next().get( "item" ) );
        }

        TestUtil.testArray( seen.iterator(), new RDFNode[] {TestUtil.resourceFixture( null, "b" ),
                                                            TestUtil.resourceFixture( null, "c" ),
                                                            TestUtil.resourceFixture( null, "http://fu.bar/test#g" )} );
    }

    @Test
    public void testDescribe1() {
        Model m = TestUtil.modelFixture( ":a :p :b ; :p :c. :e :p1 :g." );
        Model d = QueryUtil.describe( m, "describe ?item {?item :p ?_}" );

        assertTrue( TestUtil.modelFixture( ":a :p :b ; :p :c." ).isIsomorphicWith( d  ) );
    }

    @Test
    public void testDescribe2() {
        Model m = TestUtil.modelFixture( ":a :p :b ; :p :c. :e :p1 :g." );
        Model d = QueryUtil.describe( m, "describe ?item {?item ?pred ?_}", null, "pred", TestUtil.propertyFixture( m, "p" ) );

        assertTrue( TestUtil.modelFixture( ":a :p :b ; :p :c." ).isIsomorphicWith( d  ) );
    }

    /***********************************/
    /* Instance variables              */
    /***********************************/

    /***********************************/
    /* Constructors                    */
    /***********************************/

    /***********************************/
    /* External signature methods      */
    /***********************************/

    /***********************************/
    /* Internal implementation methods */
    /***********************************/

    /***********************************/
    /* Inner class definitions         */
    /***********************************/

}

