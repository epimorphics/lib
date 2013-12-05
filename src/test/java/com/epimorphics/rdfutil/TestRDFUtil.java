/******************************************************************
 * File:        TestRDFUitl.java
 * Created by:  Dave Reynolds
 * Created on:  12 Aug 2012
 *
 * (c) Copyright 2012, Epimorphics Limited
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

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.util.TestUtil;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;

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
        
        n = RDFUtil.asRDFNode("foo@en");
        l = ResourceFactory.createLangLiteral( "foo", "en" );
        assertEquals(l,  n);
        
        n = RDFUtil.asRDFNode("foo@en-GB");
        l = ResourceFactory.createLangLiteral( "foo", "en-GB" );
        assertEquals(l,  n);
    }
    
    @Test
    public void testLanguageMatch() {
        Resource r = TestUtil.resourceFixture(":r skos:prefLabel 'foo'; skos:altLabel 'bar'.");
        assertEquals("foo", RDFUtil.getLabel(r, "en"));
        
        r = TestUtil.resourceFixture(":r skos:prefLabel 'foo'@fr; skos:altLabel 'bar'.");
        assertEquals("bar", RDFUtil.getLabel(r, "en"));
        
        r = TestUtil.resourceFixture(":r rdfs:label 'foo-fr'@fr; rdfs:label 'foo-en'@en;");
        assertEquals("foo-en", RDFUtil.getLabel(r, "en"));
    }
    
    @Test
    public void testSerialize() {
        doTestSerialize(TestUtil.resourceFixture( null, "foo" ));
        doTestSerialize( ResourceFactory.createPlainLiteral("bar"));
        doTestSerialize( ResourceFactory.createLangLiteral("foobar", "en"));
        doTestSerialize( ResourceFactory.createTypedLiteral(42) );
        assertTrue( RDFUtil.deserialize( RDFUtil.serlialize(ResourceFactory.createResource()) ).isAnon() );
    }
    
    private void doTestSerialize(RDFNode node) {
        assertEquals(node, RDFUtil.deserialize( RDFUtil.serlialize(node) ) );
    }
    
    @Test
    public void testNSMapping() {
        Model original = FileManager.get().loadModel("testModel.ttl");
        Model expected = FileManager.get().loadModel("mappedTestModel.ttl");
        Model mapped = RDFUtil.mapNamespace(original, "http://www.epimporphics.com/examples#", "http://example.com/test#");
        assertTrue( mapped.isIsomorphicWith(expected) );
    }
}
