/*****************************************************************************
 * File:    RDFNodeWrapperTest.java
 * Project: epimorphics-lib
 * Created: 15 Sep 2014
 * By:      ian
 *
 * Copyright (c) 2014 Epimorphics Ltd. All rights reserved.
 *****************************************************************************/

package com.epimorphics.rdfutil;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 * Unit tests for {@link RDFNodeWrapper}
 */
public class RDFNodeWrapperTest
{
    /***********************************/
    /* Constants                       */
    /***********************************/

    private static final String NS = "http://example.com/test/";

    /***********************************/
    /* Static variables                */
    /***********************************/

    @SuppressWarnings( value = "unused" )
    private static final Logger log = LoggerFactory.getLogger( RDFNodeWrapperTest.class );

    /***********************************/
    /* Instance variables              */
    /***********************************/

    private ModelWrapper modelw;
    private RDFNodeWrapper testRes;
    private RDFNodeWrapper testLit;

    /***********************************/
    /* Constructors                    */
    /***********************************/

    /***********************************/
    /* External signature methods      */
    /***********************************/

    @Before
    public void setUp() throws Exception {
        Model m = ModelFactory.createDefaultModel();
        modelw = new ModelWrapper( m );
        testRes = new RDFNodeWrapper( modelw, m.createResource( NS + "r" ) );
        testLit = new RDFNodeWrapper( modelw, m.createLiteral( "testing 123" ) );
    }

    @Test
    public void testHashCode() {
        assertEquals( testRes.node.hashCode(), testRes.hashCode() );
        assertEquals( testLit.node.hashCode(), testLit.hashCode() );
    }

    @Test
    public void testEqualsObject() {
        RDFNodeWrapper n = new RDFNodeWrapper( modelw, ResourceFactory.createResource( NS + "r") );
        assertTrue( testRes.equals( n ));
        assertFalse( testLit.equals( n ));

        n = new RDFNodeWrapper( modelw, ResourceFactory.createPlainLiteral( "testing 123") );
        assertFalse( testRes.equals( n ));
        assertTrue( testLit.equals( n ));
    }

    @Test
    public void testToString() {
        assertEquals( "testing 123", testLit.toString() );
        assertEquals( "http://example.com/test/r", testRes.toString() );

        modelw.getModel().setNsPrefix( "foo", NS );
        assertEquals( "foo:r", testRes.toString() );
    }

    @Test
    public void testAsTurtle1() {
        assertEquals( "<http://example.com/test/r>", testRes.asTurtle() );
    }

    @Test
    public void testAsTurtle2() {
        modelw.getModel().setNsPrefix( "foo", NS );
        assertEquals( "foo:r", testRes.toString() );
    }

    @Test
    public void testAsTurtle3() {
        Resource anon = modelw.getModel().createResource();
        anon.addProperty( RDF.type, RDF.Statement );

        assertEquals( "[<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement>; ]",
                      new RDFNodeWrapper( modelw, anon ).asTurtle() );
    }


    @Test
    public void testAsTurtle4() {
        assertEquals( "\"testing 123\"", testLit.asTurtle() );
    }

    @Test
    public void testGetModelW() {
        assertSame( modelw, testRes.getModelW() );
    }

    @Test
    public void testAsRDFNode() {
        Resource r = modelw.getModel().createResource( NS + "r" );
        Literal l = modelw.getModel().createLiteral( "testing 123" );

        assertEquals( r, testRes.asRDFNode() );
        assertEquals( l, testLit.asRDFNode() );
    }

    @Test
    public void testIsLiteral() {
        assertTrue( testLit.isLiteral() );
        assertFalse( testRes.isLiteral() );
    }

    @Test
    public void testAsLiteral0() {
        assertEquals( modelw.getModel().createLiteral( "testing 123" ), testLit.asLiteral() );
    }

    @Test( expected=LiteralRequiredException.class)
    public void testAsLiteral1() {
        testRes.asLiteral();
    }

    @Test
    public void testIsList() {
        assertFalse( testRes.isList() );
        assertFalse( testLit.isList() );

        Model m = modelw.getModel();
        RDFList l = m.createList( new RDFNode[] {testRes.asRDFNode()} );
        RDFNodeWrapper ln = new RDFNodeWrapper( modelw, l );

        assertTrue( ln.isList() );

        List<RDFNodeWrapper> ll = ln.asList();
        assertEquals( 1, ll.size() );
        assertEquals( testRes, ll.get( 0 ));
    }

    @Test
    public void testGetLexicalForm() {
        assertEquals( "testing 123", testLit.getLexicalForm() );
        assertEquals( "http://example.com/test/r", testRes.getLexicalForm() );
    }

    @Test
    public void testGetName() {
        assertEquals( "r", testRes.getName() );

        modelw.getModel().add( testRes.asResource(), RDFS.label, "I am Groot" );
        assertEquals( "I am Groot", testRes.getName() );
    }

    @Test
    public void testGetNameFixLocalNameCamelCase() {
        RDFNodeWrapper bw = new RDFNodeWrapper( modelw, ResourceFactory.createResource( NS + "bathingWater" ) );
        assertEquals( "bathing water", bw.getName() );
    }

    @Test
    public void testGetNameFixLocalNameCamelCaseMany() {
        RDFNodeWrapper bw = new RDFNodeWrapper( modelw, ResourceFactory.createResource( NS + "bathingWaterSampleLocation" ) );
        assertEquals( "bathing water sample location", bw.getName() );
    }

    @Test
    public void testGetNameFixLocalNameHyphen() {
        RDFNodeWrapper bw = new RDFNodeWrapper( modelw, ResourceFactory.createResource( NS + "bathing-water" ) );
        assertEquals( "bathing water", bw.getName() );
    }

    @Test
    public void testGetNameFixLocalNameHyphenMany() {
        RDFNodeWrapper bw = new RDFNodeWrapper( modelw, ResourceFactory.createResource( NS + "bathing-water-sample-location" ) );
        assertEquals( "bathing water sample location", bw.getName() );
    }

    @Test
    public void testGetNameFixLocalNameMixed() {
        RDFNodeWrapper bw = new RDFNodeWrapper( modelw, ResourceFactory.createResource( NS + "bathingWater-sampleLocation" ) );
        assertEquals( "bathing water sample location", bw.getName() );
    }

    @Test
    public void testGetNameDontFixLabelCamelCase() {
        RDFNodeWrapper bw = new RDFNodeWrapper( modelw, ResourceFactory.createResource( NS + "bathing-water" ) );
        modelw.getModel().add( bw.asResource(), RDFS.label, "this is a bathingWater" );
        assertEquals( "this is a bathingWater", bw.getName() );
    }

    @Test
    public void testGetNameLabelUnchanged() {
        modelw.getModel().add( testRes.asResource(), RDFS.label, "Groot" );
        assertEquals( "Groot", testRes.getName() );
    }

    @Test
    public void testGetLanguage() {
        assertEquals( "", testLit.getLanguage() );
    }

    @Test
    public void testGetValue() {
        Literal v = modelw.getModel().createTypedLiteral( 42 );

        assertEquals( 42, new RDFNodeWrapper( modelw, v ).getValue() );
    }

    @Test
    public void testGetDatatype() {
        Literal v = modelw.getModel().createTypedLiteral( 42 );

        assertEquals( "http://www.w3.org/2001/XMLSchema#int", new RDFNodeWrapper( modelw, v ).getDatatype().getURI() );
    }

    @Test
    public void testIsResource() {
        assertTrue( testRes.isResource() );
        assertFalse( testLit.isResource() );
    }

    @Test
    public void testIsAnon() {
        assertFalse( testRes.isAnon() );

        Resource anon = modelw.getModel().createResource();
        assertTrue( new RDFNodeWrapper( modelw, anon ).isAnon() );
    }

    @Test
    public void testAsResource0() {
        assertEquals( NS + "r", testRes.asResource().getURI() );
    }

    @Test( expected=ResourceRequiredException.class )
    public void testAsResource1() {
        testLit.asResource();
    }

    @Test
    public void testGetURI() {
        assertEquals( NS + "r", testRes.getURI() );
    }

    @Test
    public void testGetShortURI() {
        assertEquals( NS + "r", testRes.getShortURI() );
        modelw.getModel().setNsPrefix( "foo", NS );
        assertEquals( "foo:r", testRes.getShortURI() );
    }

    @Test
    public void testToProperty() {
        Property p = modelw.getModel().createProperty( NS + "p" );

        assertEquals( p, testRes.toProperty( NS + "p" ));
        modelw.getModel().setNsPrefix( "foo", NS );
        assertEquals( p, testRes.toProperty( "foo:p" ));
        assertEquals( p, testRes.toProperty( p ));
        assertEquals( p, testRes.toProperty( ResourceFactory.createResource( NS + "p" ) ));
        assertEquals( p, testRes.toProperty( new RDFNodeWrapper( modelw, ResourceFactory.createResource( NS + "p" ) ) ) );

    }

    @Test
    public void testGet() {
        modelw.getModel().add( testRes.asResource(), RDFS.label, "foo" );
        modelw.getModel().setNsPrefix( "rdfs", RDFS.getURI() );

        assertEquals( "foo", testRes.get( "rdfs:label" ).getLexicalForm() );
    }

    @Test
    public void testGetPropertyValue() {
        modelw.getModel().add( testRes.asResource(), RDFS.label, "foo" );
        modelw.getModel().setNsPrefix( "rdfs", RDFS.getURI() );

        assertEquals( "foo", testRes.get( "rdfs:label" ).getLexicalForm() );
    }

    @Test
    public void testGetLocalizedValue() {
        Model m = modelw.getModel();
        m.add( testRes.asResource(), RDFS.label, m.createLiteral( "foo", "en" ) );
        m.add( testRes.asResource(), RDFS.label, m.createLiteral( "le foo", "fr" ) );

        modelw.setLanguage( "en" );
        assertEquals( "foo", testRes.getLocalizedValue( RDFS.label ));

        modelw.setLanguage( "fr" );
        assertEquals( "le foo", testRes.getLocalizedValue( RDFS.label ));
    }

    @Test
    public void testHasResourceValue() {
        Model m = modelw.getModel();
        m.add( testRes.asResource(), RDFS.seeAlso, m.createResource( NS + "foo" ) );
        m.setNsPrefix( "ex", NS );

        assertTrue( testRes.hasResourceValue( RDFS.seeAlso, "ex:foo" ));
    }

    @Test
    public void testListPropertyValues() {
        Model m = modelw.getModel();
        m.add( testRes.asResource(), RDFS.seeAlso, m.createResource( NS + "foo" ) );
        m.setNsPrefix( "ex", NS );

        List<RDFNodeWrapper> vs = testRes.listPropertyValues( RDFS.seeAlso );
        assertEquals( 1, vs.size() );
        assertEquals( NS + "foo", vs.get( 0 ).getURI() );
    }

    @Test
    public void testListProperties() {
        Model m = modelw.getModel();
        m.add( testRes.asResource(), RDFS.seeAlso, m.createResource( NS + "foo" ) );
        m.setNsPrefix( "ex", NS );

        List<PropertyValue> vs = testRes.listProperties();
        assertEquals( 1, vs.size() );
        assertEquals( "ex:foo", vs.get( 0 ).getValues().get(0).getShortURI() );
        assertEquals( RDFS.seeAlso.getURI(), vs.get(0).getProp().getURI() );
    }

    @Test
    public void testListInLinksObject() {
        Model m = modelw.getModel();
        m.add( m.createResource( NS + "foo" ), RDFS.seeAlso, testRes.asRDFNode() );
        m.setNsPrefix( "ex", NS );

        List<RDFNodeWrapper> inLinks = testRes.listInLinks( RDFS.seeAlso );
        assertEquals( 1, inLinks.size() );
        assertEquals( NS + "foo", inLinks.get(  0  ).getURI() );
    }

    @Test
    public void testListInLinks() {
        Model m = modelw.getModel();
        m.add( m.createResource( NS + "foo" ), RDFS.seeAlso, testRes.asRDFNode() );
        m.setNsPrefix( "ex", NS );

        List<PropertyValue> inLinks = testRes.listInLinks();
        assertEquals( 1, inLinks.size() );
        assertEquals( NS + "foo", inLinks.get(  0  ).getValues().get( 0 ).getURI() );
        assertEquals( RDFS.seeAlso.getURI(), inLinks.get( 0 ).getProp().getURI() );
    }

    @Test
    public void testFirstValueOf() {
        Model m = modelw.getModel();
        m.add( testRes.asResource(), RDFS.seeAlso, m.createResource( NS + "foo" ) );
        m.setNsPrefix( "ex", NS );
        m.setNsPrefix( "rdfs", RDFS.getURI() );

        assertEquals( NS + "foo", testRes.firstValueOf( "foo:bar,rdfs:type,rdfs:seeAlso,rdf:first" ).getURI() );
    }

    @Test
    public void testConnectedNodes() {
        Model m = modelw.getModel();
        m.setNsPrefix( "ex", NS );
        m.setNsPrefix( "rdfs", RDFS.getURI() );

        Property p0 = m.getProperty( NS + "p0" );
        Property p1 = m.getProperty( NS + "p1" );

        Resource a = m.createResource( NS + "a" );
        Resource b = m.createResource( NS + "b" );
        Resource c = m.createResource( NS + "c" );

        m.add( testRes.asResource(), p0, b );
        m.add( b, p1, a );
        m.add( b, p1, c );

        List<RDFNodeWrapper> cn = testRes.connectedNodes( "ex:p0/ex:p1" );
        assertEquals( 2, cn.size() );
        assertTrue( cn.contains( new RDFNodeWrapper( modelw, a ) ));
        assertTrue( cn.contains( new RDFNodeWrapper( modelw, c ) ));
    }

    @Test
    public void testClosure() {
        Model m = modelw.getModel();
        m.setNsPrefix( "ex", NS );
        m.setNsPrefix( "rdfs", RDFS.getURI() );

        Property p0 = m.getProperty( NS + "p0" );
        Property p1 = m.getProperty( NS + "p1" );

        Resource a = m.createResource();
        Resource b = m.createResource( NS + "b" );
        Resource c = m.createResource( NS + "c" );
        Resource d = m.createResource( NS + "d" );

        m.add( testRes.asResource(), p0, a );
        m.add( a, p1, b );
        m.add( testRes.asResource(), p1, c );
        m.add( c, p1, d );

        ModelWrapper mw = testRes.closure();
        assertEquals( 3, mw.getModel().size() );
    }

    @Test
    public void testClosureModelWrapper() {
        Model m = modelw.getModel();
        m.setNsPrefix( "ex", NS );
        m.setNsPrefix( "rdfs", RDFS.getURI() );

        Property p0 = m.getProperty( NS + "p0" );
        Property p1 = m.getProperty( NS + "p1" );

        Resource a = m.createResource();
        Resource b = m.createResource( NS + "b" );
        Resource c = m.createResource( NS + "c" );
        Resource d = m.createResource( NS + "d" );

        m.add( testRes.asResource(), p0, a );
        m.add( a, p1, b );
        m.add( testRes.asResource(), p1, c );
        m.add( c, p1, d );

        Model target = ModelFactory.createDefaultModel();
        ModelWrapper mw = new ModelWrapper( target );

        testRes.closure( mw );
        assertEquals( 3, target.size() );
    }

    /***********************************/
    /* Internal implementation methods */
    /***********************************/

    /***********************************/
    /* Inner class definitions         */
    /***********************************/

}

