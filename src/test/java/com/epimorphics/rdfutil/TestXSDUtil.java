/******************************************************************
 * File:        TestXSDUtil.java
 * Created by:  Dave Reynolds
 * Created on:  11 Aug 2012
 * 
 * (c) Copyright 2012, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.rdfutil;

import org.junit.Test;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import static org.junit.Assert.*;
import static com.epimorphics.rdfutil.XSDUtil.commonSuperDatatype;;

public class TestXSDUtil {

    @Test
    public void testBasics() {
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
}
