/******************************************************************
 * File:        TestJSONRender.java
 * Created by:  Dave Reynolds
 * Created on:  16 Dec 2011
 *
 * (c) Copyright 2011, Epimorphics Limited
 *****************************************************************/

package com.epimorphics.json;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;

import com.epimorphics.json.RDFJSONModReader;
import com.epimorphics.json.RDFJSONModWriter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileUtils;
import static org.junit.Assert.*;


public class TestJSONRender {
    public static final String NS = "http://www.epimorphics.com/test#";
    public static final String PREFIX = "@prefix ns: <" + NS + "> . \n";

    @Test
    public void testBasicModel() {
        doTestRender("Plain literal", "ns:a ns:q 'foo' ." );
        doTestRender("Lang", "ns:a ns:q 'foo'@it ." );
        doTestRender("Integer",  "ns:a ns:q 1 ." );
        doTestRender("Typed literal", "ns:a ns:q '1.4'^^xsd:decimal ." );
        doTestRender("URIs",  "ns:a ns:q ns:b ." );
        doTestRender("Simple bNode",  "ns:a ns:q [] ." );
        doTestRender("bNode ref",  "_:1 ns:p _:2 . _:2 ns:p 'foo' ." );
        doTestRender("bNode cycle",  "_:1 ns:p _:2 . _:2 ns:p _:1 ." );
        doTestRender("Multiple values", "ns:a ns:p 'foo', 'bar', 'baz' ." );
        doTestRender("Arrays", "ns:a ns:p (1 2 3) ." );
    }


    @Test
    public void testFullModels() {
        doTestRender("Como1", "ns:a ns:p 'foo'; ns:nest (ns:b ns:c). ns:b rdfs:label 'b'. ns:c rdfs:label 'c', 'c-alt'.");
    }


    protected void doTestRender(String msg, String source) {
        String fullsource =
            "@prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#> .\n" +
            "@prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
            "@prefix xsd:     <http://www.w3.org/2001/XMLSchema#> .\n" +
            "@prefix owl:     <http://www.w3.org/2002/07/owl#> .\n" +
            PREFIX +
            source;

        Model model = ModelFactory.createDefaultModel();
        model.read(new StringReader(fullsource), null, FileUtils.langTurtle);

        StringWriter sw = new StringWriter();
        RDFJSONModWriter writer = new RDFJSONModWriter( sw );
        writer.write(model);
        writer.finish();

        String json = sw.getBuffer().toString();
//        System.out.println("JSON was:\n");
//        System.out.println(json);

        Model result = new RDFJSONModReader().parse( json );
//        result.write(System.out, FileUtils.langTurtle);
        assertTrue(msg, result.isIsomorphicWith(model));
    }

}

