/******************************************************************
 * File:        TypeUtilTest.java
 * Created by:  Dave Reynolds
 * Created on:  11 Jan 2015
 * 
 * (c) Copyright 2015, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.rdfutil;

import org.junit.Test;

import static org.junit.Assert.*;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.XSD;

public class TypeUtilTest {

    @Test
    public void testExplicitConversion() {
        checkExplicit("1234", "int", true);
        checkExplicit("foo", "int", false);
        checkExplicit("foo", null, true);
        checkExplicit("2014-01-10", "date", true);
        checkExplicit("2014-01-10T15:12:34", "date", false);
        checkExplicit("2014-01-10T15:12:34", "dateTime", true);
    }
    
    private void checkExplicit(String lex, String type, boolean ok) {
        String typeURI = type == null ? null : XSD.getURI() + type;
        try {
            RDFNode node = TypeUtil.asTypedValue(lex, typeURI);
            assertTrue(node.isLiteral());
            Literal l = node.asLiteral();
            assertEquals(lex, l.getLexicalForm());
            if (type != null) {
                assertEquals(typeURI, l.getDatatypeURI());
            }
        } catch (TypeUtil.IllegalFormatException e) {
            assertFalse(ok);
        }
    }
    
    @Test
    public void testImplicitConverstion() {
        checkImplicit("foo", null);
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
}
