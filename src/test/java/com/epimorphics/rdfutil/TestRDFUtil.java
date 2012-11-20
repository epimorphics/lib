/******************************************************************
 * File:        TestRDFUitl.java
 * Created by:  Dave Reynolds
 * Created on:  12 Aug 2012
 *
 * (c) Copyright 2012, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.rdfutil;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.util.TestUtil;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.*;

public class TestRDFUtil {

    @Test
    public void testLocalname() {
        Resource foo = ResourceFactory.createResource("http://example.com/foo");
        assertEquals("foo", RDFUtil.getLocalname(foo));
        assertEquals("http://example.com/", RDFUtil.getNamespace(foo));
        foo = ResourceFactory.createResource("http://example.com#foo");
        assertEquals("foo", RDFUtil.getLocalname(foo));
        assertEquals("http://example.com#", RDFUtil.getNamespace(foo));
        foo = ResourceFactory.createResource("http://example.com/foo#1");
        assertEquals("1", RDFUtil.getLocalname(foo));
        assertEquals("http://example.com/foo#", RDFUtil.getNamespace(foo));
    }

    @Test
    public void testAsNode() {
        Resource r = TestUtil.resourceFixture( null, "foo" );
        assertSame( r, RDFUtil.asRDFNode( r ));

        Literal l = ResourceFactory.createPlainLiteral( "foo" );
        assertSame( l, RDFUtil.asRDFNode( l ));

        RDFNode n = RDFUtil.asRDFNode( TestUtil.baseURIFixture() + "foo" );
        assertTrue( n.isResource() );
        assertEquals( r, n );

        n = RDFUtil.asRDFNode( "foo" );
        assertTrue( n.isLiteral() );
        assertEquals( ResourceFactory.createPlainLiteral( "foo" ), n );

        n = RDFUtil.asRDFNode( 123 );
        assertTrue( n.isLiteral() );
        assertEquals( ResourceFactory.createTypedLiteral( "123", XSDDatatype.XSDint ), n );

        n = RDFUtil.asRDFNode( null );
        assertTrue( n.isResource() );
        assertTrue( n.isAnon() );
    }
}
