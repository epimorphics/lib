/******************************************************************
 * File:        TestSPARQLUpdateWriter.java
 * Created by:  Dave Reynolds
 * Created on:  7 Aug 2011
 * 
 * (c) Copyright 2011, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.rdfutil;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

import junit.framework.TestCase;

public class TestSPARQLUpdateWriter extends TestCase {

    public void testBasic() throws IOException {
        Model source = ModelFactory.createDefaultModel();
        String NS = "http://www.epimorphics.com/test#";
        Property p = source.createProperty(NS + "p");
        Resource r = source.createResource(NS +"r")
            .addLiteral(p, 42)
            .addProperty(RDFS.comment, "A test resource");
        source.createResource(NS +"r2")
            .addLiteral(p, 43)
            .addProperty(RDFS.comment, "Another test resource")
            .addProperty(RDFS.seeAlso, r);
        source.setNsPrefix("rdfs", RDFS.getURI());
        source.setNsPrefix("test", NS);
        
        StringWriter testout = new StringWriter();
        SPARQLUpdateWriter uw = new SPARQLUpdateWriter();
        SPARQLUpdateWriter.writeUpdatePrefixes(source, testout);
        testout.close();
        assertTrue(testout.toString().contains("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"));
        assertTrue(testout.toString().contains("PREFIX test: <http://www.epimorphics.com/test#>"));
        
        testout = new StringWriter();
        uw.writeUpdateBody(source, testout);
        testout.close();
        String prelude = "@prefix rdfs: <" + RDFS.getURI() +"> .\n@prefix test: <" + NS + "> .\n";
        StringReader in = new StringReader(prelude + testout.toString());
        Model got = ModelFactory.createDefaultModel();
        got.read(in, null, "Turtle");
        assertTrue(got.isIsomorphicWith(source));
    }
}
