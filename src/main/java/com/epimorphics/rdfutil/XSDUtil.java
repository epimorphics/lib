/******************************************************************
 * File:        XSDUtil.java
 * Created by:  Dave Reynolds
 * Created on:  11 Aug 2012
 * 
 * (c) Copyright 2012, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.rdfutil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

public class XSDUtil {
    
    /**
     * Return the common super type of two xsd type datatypes or return null
     * if they are not compatible.
     */
    public static XSDDatatype commonSuperDatatype(XSDDatatype t1, XSDDatatype t2) {
        if (t1 == t2) return t1;
        Set<XSDDatatype> parents = new HashSet<XSDDatatype>();
        XSDDatatype parent = t1;
        while (parent != null) {
            parents.add(parent);
            parent = XSDTree.get(parent);
        }
        if (parents.isEmpty()) return null;
        XSDDatatype probe = t2;
        while (probe != null) {
            if (parents.contains(probe)) return probe;
            probe = XSDTree.get(probe);
        }
        return null;
    }
    
    private static Map<XSDDatatype, XSDDatatype>XSDTree = new HashMap<XSDDatatype, XSDDatatype>();
    static {
        XSDTree.put( XSDDatatype.XSDunsignedByte, XSDDatatype.XSDunsignedShort );
        XSDTree.put( XSDDatatype.XSDunsignedShort, XSDDatatype.XSDunsignedInt );
        XSDTree.put( XSDDatatype.XSDunsignedInt, XSDDatatype.XSDunsignedLong );
        XSDTree.put( XSDDatatype.XSDunsignedLong , XSDDatatype.XSDnonNegativeInteger  );
        XSDTree.put( XSDDatatype.XSDnonNegativeInteger , XSDDatatype.XSDinteger );
        XSDTree.put( XSDDatatype.XSDpositiveInteger , XSDDatatype.XSDnonNegativeInteger  );
        XSDTree.put( XSDDatatype.XSDnegativeInteger , XSDDatatype.XSDnonPositiveInteger  );
        XSDTree.put( XSDDatatype.XSDnonPositiveInteger , XSDDatatype.XSDinteger  );
        XSDTree.put( XSDDatatype.XSDinteger , XSDDatatype.XSDdecimal  );
        XSDTree.put( XSDDatatype.XSDbyte , XSDDatatype.XSDshort  );
        XSDTree.put( XSDDatatype.XSDshort , XSDDatatype.XSDint  );
        XSDTree.put( XSDDatatype.XSDint, XSDDatatype.XSDlong );
        XSDTree.put( XSDDatatype.XSDlong, XSDDatatype.XSDinteger );
        XSDTree.put( XSDDatatype.XSDENTITY , XSDDatatype.XSDNCName  );
        XSDTree.put( XSDDatatype.XSDID, XSDDatatype.XSDNCName );
        XSDTree.put( XSDDatatype.XSDIDREF, XSDDatatype.XSDNCName );
        XSDTree.put( XSDDatatype.XSDNCName, XSDDatatype.XSDName );
        XSDTree.put( XSDDatatype.XSDName , XSDDatatype.XSDtoken  );
        XSDTree.put( XSDDatatype.XSDlanguage , XSDDatatype.XSDtoken );
        XSDTree.put( XSDDatatype.XSDNMTOKEN , XSDDatatype.XSDtoken );
        XSDTree.put( XSDDatatype.XSDtoken, XSDDatatype.XSDnormalizedString );
        XSDTree.put( XSDDatatype.XSDnormalizedString, XSDDatatype.XSDstring  );
    }
  
}
