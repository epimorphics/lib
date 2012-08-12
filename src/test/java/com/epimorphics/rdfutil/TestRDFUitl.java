/******************************************************************
 * File:        TestRDFUitl.java
 * Created by:  Dave Reynolds
 * Created on:  12 Aug 2012
 * 
 * (c) Copyright 2012, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.rdfutil;

import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import static org.junit.Assert.*;

public class TestRDFUitl {

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
}
