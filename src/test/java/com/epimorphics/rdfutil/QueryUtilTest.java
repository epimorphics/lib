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

import java.util.*;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epimorphics.util.PrefixUtils;
import com.epimorphics.util.TestUtil;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.*;

/**
 * <p>Unit tests for {@link QueryUtil}</p>
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

    @Test
    public void testSelectAllVar() {
        Model m = TestUtil.modelFixture( ":a :p :b ; :p :c. :e :p :g." );

        List<RDFNode> as = QueryUtil.selectAllVar( "a", m, "select * {?a :p ?item}", null ) ;

        TestUtil.testArray( as, new RDFNode[] {TestUtil.resourceFixture( null, "a" ),
                                                TestUtil.resourceFixture( null, "e" )} );
    }

    @Test
    public void testSelectFirstVar() {
        Model m = TestUtil.modelFixture( ":a :p :b ; :p :c. :e :p :g." );

        RDFNode item = QueryUtil.selectFirstVar( "item", m, "select * {?item :p :g}", null ) ;

        assertEquals( TestUtil.resourceFixture( null, "e" ), item );
    }

    @Test
    public void testAsSparqlValue() {
        assertEquals( "<http://example.test/test#r>", QueryUtil.asSPARQLValue( TestUtil.resourceFixture( null, "r" ) ));
        assertTrue( QueryUtil.asSPARQLValue( ResourceFactory.createResource() ).startsWith( "_:" ) );
        assertEquals( "'123'^^<http://www.w3.org/2001/XMLSchema#int>", QueryUtil.asSPARQLValue( ResourceFactory.createTypedLiteral( 123 ) ));

        Model m = ModelFactory.createDefaultModel();
        assertEquals( "'foo'@klingon", QueryUtil.asSPARQLValue( m.createLiteral( "foo", "klingon" ) ));
        assertEquals( "'foo'", QueryUtil.asSPARQLValue( m.createLiteral( "foo" ) ));

        assertEquals( "'foo'", QueryUtil.asSPARQLValue( "foo" ));
        assertEquals( "foo:bar", QueryUtil.asSPARQLValue( "foo:bar" ));
        assertEquals( "'1234'", QueryUtil.asSPARQLValue( 1234 ));
    }

    @Test
    public void testSubstituteVars() {
        assertEquals( "select * where {?p :q 'bar'}", QueryUtil.substituteVars( "select * where {?p :q ?foo}", QueryUtil.createBindings( "foo", "bar" ) ) );

        Resource r = TestUtil.resourceFixture( null, "r" );
        assertEquals( "select * where {?p :q <http://example.test/test#r>}", QueryUtil.substituteVars( "select * where {?p :q ?foo}", QueryUtil.createBindings( "foo", r ) ) );
        assertEquals( "select * where {?food :q <http://example.test/test#r>}", QueryUtil.substituteVars( "select * where {?food :q ?foo}", QueryUtil.createBindings( "foo", r ) ) );
    }

    @Test
    public void testRemoteService() {
        ResultSet rs = QueryUtil.serviceSelectAll( "http://environment.data.gov.uk/sparql/bwq/query", "select * {?s ?p ?o} limit 1", null );
        assertTrue( rs.hasNext() );
        rs.next();
        assertFalse( rs.hasNext() );
    }
    

    @Test
    public void testPath() {
        Model m = TestUtil.modelFixture( "@prefix : <http://fu.bar/test#>.\n :a :p :c . :c :q :d, 'foo' ." );
        Resource root = m.createResource("http://fu.bar/test#a");
        List<Resource> results = QueryUtil.connectedResources(root, ":p/:q");
        TestUtil.testArray(results, new Resource[]{ m.createResource("http://fu.bar/test#d") });
        
        TestUtil.testArray(
                QueryUtil.connectedLiterals(root, ":p/:q"),
                new Literal[]{ m.createLiteral("foo") });
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

