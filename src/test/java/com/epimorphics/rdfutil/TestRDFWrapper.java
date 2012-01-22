/******************************************************************
 * File:        testRDFWrapper.java
 * Created by:  Dave Reynolds
 * Created on:  19 Apr 2011
 *
 * (c) Copyright 2011, Epimorphics Limited
 * $Id:  $
 *****************************************************************/

package com.epimorphics.rdfutil;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

import junit.framework.TestCase;

public class TestRDFWrapper extends TestCase {

    Model testModel;
    ModelWrapper mw;

    public void setUp() {
        testModel = FileManager.get().loadModel("src/test/resources/testModel.ttl");
        PrefixManager pm = new SimplePrefixManager();
        pm.registerPrefixes(testModel);
        mw = new ModelWrapper(testModel, null, pm);
    }

    public void testBasicModel() {
        assertTrue( mw.getModel().size() > 10 );

        checkList(new String[]{"Bill", "Joan", "Dave", "Duncan", "Lucy", "Colin"},
                mw.queryList("{ [a eg:Person; rdfs:label ?item]. }") );

        RDFNodeWrapper node = mw.getNode("eg:dave");
        List<Map<String, Object>> matches = mw.queryValues("{ ?this eg:age ?age. }", node );
        assertEquals(1, matches.size());
        Map<String, Object> match = matches.get(0);
        assertTrue(match.containsKey("age"));
        Object val = match.get("age");
        assertTrue(val instanceof Integer);
        assertEquals(54, ((Integer)val).intValue());

        assertEquals(2, mw.listSubjects(2).size());
        checkList( new String[]{"eg:bill", "eg:joan", "eg:dave", "eg:colin", "eg:lucy", "eg:duncan", "eg:Person", "eg:age", "eg:parentOf"},
                    mw.listSubjects(100));
    }

    public void testResourceNode() {
        RDFNodeWrapper node = mw.getNode("eg:dave");
        assertTrue( node.isResource() );
        assertEquals("Dave", node.getName());
        assertEquals("http://www.epimporphics.com/examples#dave", node.getLexicalForm());
        assertEquals(54, node.getPropertyValue("eg:age"));
        assertEquals(54, node.getPropertyValue( mw.getNode("eg:age") ));
        assertEquals(54, node.getPropertyValue( mw.getNode("eg:age").asResource() ));

        checkList( new String[]{"eg:colin", "eg:lucy", "eg:duncan"}, node.listPropertyValues("eg:parentOf"));
        checkList( new String[]{"eg:bill", "eg:joan"}, node.listInLinks("eg:parentOf"));
        checkList( new String[]{"eg:parentOf"}, node.listInLinks().keySet());
        checkList( new String[]{"rdf:type", "rdfs:label", "eg:age", "eg:parentOf"}, node.listProperties().keySet() );

        checkList( new String[]{"Bill", "Joan"},
                   node.queryList("{ ?p eg:parentOf ?this. ?p rdfs:label ?item}" ) );

        Map<String, Object> record = node.queryValues("{ ?this eg:age ?age; rdfs:label ?name. }").get(0);
        assertEquals(54, record.get("age"));
        assertEquals("Dave", record.get("name"));

        Map<String, RDFNodeWrapper>recordw = node.query("{ ?this eg:age ?age; rdfs:label ?name. }").get(0);
        assertEquals( 54,  recordw.get("age").getValue() );
    }

    public void testLiteralNode() {
        RDFNodeWrapper plain = mw.queryList("{ eg:Person rdfs:label ?item . }").get(0);
        assertTrue(plain.isLiteral());
        assertEquals("Person", plain.getLexicalForm());
        assertEquals( "", plain.getLanguage() );

        RDFNodeWrapper l = mw.queryList("{ eg:Person rdfs:comment ?item . }").get(0);
        assertTrue(l.isLiteral());
        assertEquals("Class for a person", l.getLexicalForm());
        assertEquals("en", l.getLanguage());

    }

    public void testQueryExpansion() {
        String query1 = "PREFIX up:  <http://www.epimorphics.com/vocabs/update#> SELECT * WHERE { ?u a up:Update . }";
        String query2 = "SELECT * WHERE { ?u a up:Update . }";
        String query3 = "{ ?u a up:Update . }";
        assertEquals(query1, mw.normalizeQuery(query1));
        assertEquals(query2, mw.normalizeQuery(query2));
        assertEquals(query2, mw.normalizeQuery(query3));
    }
    
    public void testEquals() {
        RDFNodeWrapper node0 = mw.getNode("eg:dave");
        RDFNodeWrapper node1 = mw.getNode("eg:dave");
        RDFNodeWrapper node2 = mw.getNode("eg:ian");
        assertTrue( node0.equals( node1 ));
        assertTrue( node1.equals( node0 ));

        assertFalse( node0.equals( node2 ));
        assertFalse( node2.equals( node0 ));
    }

    private void checkList(String[] expected, Collection<?> results) {
        Set<Object> resultSet = new HashSet<Object>();
        for (Object r : results) {
            resultSet.add( r.toString() );
        }

        Set<Object> expectedSet = new HashSet<Object>();
        for (Object e : expected) expectedSet.add(e);

        assertEquals(expectedSet, resultSet);
    }

    @SuppressWarnings("unused")
    private void printList(List<?> values) {
        System.out.println("List is");
        for (Object value : values) {
            System.out.println(" - " + value);
        }
    }

    @SuppressWarnings("unused")
    private void printMap(List<Map<String, Object>> mapl) {
        System.out.println("Mapl is ");
        for( Map<String,Object> map: mapl) {
            System.out.println("> ");
            for (Entry<String, Object> entry : map.entrySet()) {
                System.out.println("  " + entry.getKey() + " = " + entry.getValue());
            }
        }
    }
}

