/******************************************************************
 * File:        TypeUtilTest.java
 * Created by:  Dave Reynolds
 * Created on:  11 Jan 2015
 * 
 * (c) Copyright 2015, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.rdfutil;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

public class TypeUtilTest {

    @Test
    public void testExplicitConversion() {
        checkExplicit("1234", "int", "int", true);
        checkExplicit("foo", "int", "int", false);
        checkExplicit("foo", null, null, true);
        checkExplicit("foo", "", "string", true);
        checkExplicit("1234", "", "string", true);
        checkExplicit("2014-01-10", "date", "date", true);
        checkExplicit("2014-01-10T15:12:34", "date", "date", false);
        checkExplicit("2014-01-10T15:12:34", "dateTime", "dateTime", true);
        
        checkExplicit("1234", null, "int", true);
        checkExplicit("foo", null, null, true);
        checkExplicit("2014-01-10", null, "date", true);
        checkExplicit("2014-01-10T15:12:34", null, "date", false);
        checkExplicit("2014-01-10T15:12:34", null, "dateTime", true);
    }
    
    private void checkExplicit(String lex, String type, String expected, boolean ok) {
        String typeURI = type == null ? null : type.isEmpty() ? TypeUtil.PLAIN_LITERAL_URI :  XSD.getURI() + type;
        String eTypeURI = expected == null ? null : XSD.getURI() + expected;
        try {
            RDFNode node = TypeUtil.asTypedValue(lex, typeURI);
            assertTrue(node.isLiteral());
            Literal l = node.asLiteral();
            assertEquals(lex, l.getLexicalForm());
            if (type != null) {
                assertEquals(eTypeURI, l.getDatatypeURI());
            }
        } catch (TypeUtil.IllegalFormatException e) {
            assertFalse(ok);
        }
    }
    
    @Test
    public void testImplicitConverstion() {
        checkImplicit("foo", "string");
        checkImplicit("1234", "integer");
        checkImplicit("1234.56", "decimal");
        checkImplicit("2015-01-10", "date");
        checkImplicit("2014-01-10T15:12:34", "dateTime");
        checkImplicit("2014-01", "gYearMonth");
    }
    
    private void checkImplicit(String lex, String expectedType) {
        String typeURI = expectedType == null ? null : XSD.getURI() + expectedType;
        RDFNode node = TypeUtil.asTypedValue(lex);
        assertTrue(node.isLiteral());
        Literal l = node.asLiteral();
        assertEquals(lex, l.getLexicalForm());
        if (expectedType == null) {
            assertNull(l.getDatatype());
        } else {
            assertEquals(typeURI, l.getDatatypeURI());
        }
    }
    
    private static final String URI = "http://example.com/test";
    @Test
    public void testResourceCase() {
        RDFNode node = TypeUtil.asTypedValue(URI);
        assertTrue(node.isResource());
        assertEquals(URI, node.asResource().getURI());
        
        node = TypeUtil.asTypedValue(URI, RDFS.Resource.getURI());
        assertTrue(node.isResource());
        assertEquals(URI, node.asResource().getURI());
    }
}
