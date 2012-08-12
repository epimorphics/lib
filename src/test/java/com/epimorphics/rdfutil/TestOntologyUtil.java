/******************************************************************
 * File:        TestXSDUtil.java
 * Created by:  Dave Reynolds
 * Created on:  11 Aug 2012
 * 
 * (c) Copyright 2012, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.rdfutil;

import java.util.List;

import org.junit.Test;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

import static org.junit.Assert.*;
import static com.epimorphics.rdfutil.OntologyUtil.*;

public class TestOntologyUtil {

    @Test
    public void testXSD() {
        assertEquals(XSDDatatype.XSDinteger, commonSuperDatatype(XSDDatatype.XSDbyte, XSDDatatype.XSDunsignedInt) );
        assertEquals(XSDDatatype.XSDnonNegativeInteger, commonSuperDatatype(XSDDatatype.XSDpositiveInteger, XSDDatatype.XSDunsignedByte) );
        assertEquals(XSDDatatype.XSDinteger, commonSuperDatatype(XSDDatatype.XSDnegativeInteger, XSDDatatype.XSDpositiveInteger) );
        assertEquals(XSDDatatype.XSDdecimal, commonSuperDatatype(XSDDatatype.XSDinteger, XSDDatatype.XSDdecimal) );
        assertEquals(XSDDatatype.XSDNCName, commonSuperDatatype(XSDDatatype.XSDID, XSDDatatype.XSDENTITY) );
        assertEquals(XSDDatatype.XSDtoken, commonSuperDatatype(XSDDatatype.XSDID, XSDDatatype.XSDNMTOKEN) );
        assertEquals(XSDDatatype.XSDnormalizedString, commonSuperDatatype(XSDDatatype.XSDnormalizedString, XSDDatatype.XSDtoken) );
        assertEquals(XSDDatatype.XSDstring, commonSuperDatatype(XSDDatatype.XSDstring, XSDDatatype.XSDtoken) );
        
        assertNull( commonSuperDatatype(XSDDatatype.XSDdate, XSDDatatype.XSDdateTime) );
        assertNull( commonSuperDatatype(XSDDatatype.XSDstring, XSDDatatype.XSDint) );
    }
    
    @Test
    public void testSuperClassTraversal() {
        String NS = "http://www.epimorophics.com/test#";
        Model test = ModelFactory.createDefaultModel();
        Resource A = test.createResource(NS + "A");
        Resource B = test.createResource(NS + "B");
        Resource C = test.createResource(NS + "C");
        Resource D = test.createResource(NS + "D");
        Resource E = test.createResource(NS + "E");
        Resource F = test.createResource(NS + "F");
        Resource G = test.createResource(NS + "G");
        G.addProperty(RDFS.subClassOf, E);
        F.addProperty(RDFS.subClassOf, E);
        F.addProperty(RDFS.subClassOf, D);
        D.addProperty(RDFS.subClassOf, B);
        E.addProperty(RDFS.subClassOf, B);
        B.addProperty(RDFS.subClassOf, A);
        C.addProperty(RDFS.subClassOf, A);
        
        List<Resource> traversal = depthfirstSuperclasses(G);
        assertTrue( listIs(traversal, new Resource[]{ G, E, B, A}));
        
        traversal = depthfirstSuperclasses(F);
        assertTrue(    listIs(traversal, new Resource[]{ F, E, D, B, A}) 
                    || listIs(traversal, new Resource[]{ F, D, E, B, A}) );
        
        assertEquals(E, commonSuperClass(F, G));
        assertEquals(B, commonSuperClass(D, G));
        assertEquals(A, commonSuperClass(C, G));
    }
    
    private boolean listIs(List<Resource> test, Resource[] expected) {
        if (test.size() != expected.length) return false;
        for (int i  = 0; i < expected.length; i++) {
            if ( ! test.get(i).equals(expected[i]) ) return false;
        }
        return true;
    }
    
}
