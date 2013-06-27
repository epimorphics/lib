/******************************************************************
 * File:        XSDUtil.java
 * Created by:  Dave Reynolds
 * Created on:  11 Aug 2012
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OntologyUtil {
    
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
  

    /**
     * Return the common superclass of two classes, of null if there is no common superclass in 
     * the raw ontologies (OWL inference is not used so owl:Thing might not be found).
     */
    public static Resource commonSuperClass(Resource c1, Resource c2) {
        if (c1.equals(c2)) return c1;
        Set<Resource> parents1 = new HashSet<Resource>( depthfirstSuperclasses(c1) );
        for (Resource other : depthfirstSuperclasses(c2)) {
            if (parents1.contains(other)) {
                return other;
            }
        }
        return null;
    }
    
    /**
     * Find all the superclasses of the given starting class, in depth first order.
     * Assumes the model does not have closure statements for rdfs:subClassOf.
     */
    public static List<Resource> depthfirstSuperclasses(Resource start) {
        Set<RDFNode> found = new HashSet<RDFNode>();
        List<Resource> traversal = new ArrayList<Resource>();
        found.add(start);
        List<Resource> parents = new ArrayList<Resource>();
        parents.add(start);
        while (!parents.isEmpty()) {
            List<Resource> newparents = new ArrayList<Resource>();
            for(Resource parent : parents) {
                traversal.add(parent);
                newparents.addAll( deepen(parent, found) );
            }
            parents = newparents;
        }
        return traversal;
    }
    
    private static List<Resource> deepen(Resource node, Set<RDFNode> found) {
        List<Resource> newparents = new ArrayList<Resource>();
        for (StmtIterator si = node.listProperties(RDFS.subClassOf); si.hasNext();) {
            RDFNode parent = si.next().getObject();
            if ( ! found.contains(parent)) {
                found.add(parent);
                if (parent.isResource()) {
                    newparents.add( parent.asResource() );
                }
            }
        }
        return newparents;
    }

}
