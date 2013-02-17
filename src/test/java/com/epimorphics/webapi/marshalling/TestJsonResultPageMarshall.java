/******************************************************************
 * File:        TestJsonResultPageMarshall.java
 * Created by:  Dave Reynolds
 * Created on:  15 Aug 2012
 *
 * (c) Copyright 2012, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.webapi.marshalling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;

import com.epimorphics.webapi.PageInfo;
import com.epimorphics.webapi.ResultPage;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileUtils;

public class TestJsonResultPageMarshall {

    private static String NS = "http://www.epimorphics.com/test#";

    private static String testModelDef = "@prefix : <" + NS + ">. \n" +
            ":a :label 'a' ; :child :ca .\n" +
            ":b :label 'b' ; :child :cb .\n" +
            ":c :label 'c' ; :child :cc .\n" +
            ":ca :label 'ca' .\n" +
            ":cb :label 'cb' .\n" +
            ":cc :label 'cb' .\n" +
            ":unattached :label 'floating' .\n" ;

    Model testModel;

    @Before
    public void setUp() {
        testModel = ModelFactory.createDefaultModel();
        testModel.read(new StringReader(testModelDef), null, FileUtils.langTurtle);
    }

    @Test
    public void testBasic() throws WebApplicationException, IOException {
        PageInfo dummyPage = new PageInfo( new MockUriInfo("http://localhost/example?_page=0") );
        Resource[] results = new Resource[] {
                testModel.createResource( NS + "a" ),
                testModel.createResource( NS + "b" ),
                testModel.createResource( NS + "c" ),
        };
        ResultPage resultPage = new ResultPage(dummyPage, results);

        JSONResultMarshaller marshaller = new JSONResultMarshaller();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.writeTo(resultPage, ResultPage.class, null, null, MediaType.APPLICATION_JSON_TYPE, null, out);
        out.close();

//        System.out.println(out.toString());

        JsonObject object = JSON.parse( out.toString() );
        JsonArray items = object.get("items").getAsArray();
        assertEquals(3, items.size());
        assertEquals(NS + "a", items.get(0).getAsString().value());
        assertEquals(NS + "b", items.get(1).getAsString().value());
        assertEquals(NS + "c", items.get(2).getAsString().value());

        JsonObject graph = object.get("graph").getAsObject();
        for (String probe : new String[]{"a", "b", "c", "ca", "cb", "cc", "unattached"}) {
            assertNotNull( graph.get( NS + probe ) );
        }
    }
}
