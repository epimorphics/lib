/******************************************************************
 * File:        TestSPARQLUpdateWriter.java
 * Created by:  Dave Reynolds
 * Created on:  7 Aug 2011
 * 
 * (c) Copyright 2011, Epimorphics Limited
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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;

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
