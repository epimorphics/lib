/******************************************************************
 * File:        RDFJSONModReader.java
 * Created by:  Dave Reynolds
 * Created on:  16 Dec 2011
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
 *****************************************************************/

package com.epimorphics.json;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.apache.jena.riot.RiotException;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.RDF;

/**
 * <p>Read a graph serialized using the modified JSON format. This implementation
 * is primarily for testing purposes, a more efficient and debug-friendly streaming solution such as the RIOT
 * would be possible.</p>
 *
 * <p>Not thread safe, the parse contains state, allocate a new reader for each use.</p>
 *
 * <p>In lieu of the W3C standard we are mostly following the Talis
 * <a href="http://docs.api.talis.com/platform-api/output-types/rdf-json">format</a>.
 * Unfortunately that doesn't cater for lists in a usable fashion and our primary
 * use case is outputting lists of results. So we extend the Talis format with
 * a value type "list" which is rendered in JSON as an array.</p>
 *
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class RDFJSONModReader {

    public static final String TYPE_KEY = "type";
    public static final String URI_TYPE = "uri";
    public static final String BNODE_TYPE = "bnode";
    public static final String LIST_TYPE = "array";
    public static final String LITERAL_TYPE = "literal";
    public static final String DATATYPE_KEY = "datatype";
    public static final String LANG_KEY = "lang";
    public static final String VALUE_KEY = "value";

    protected Map<String, Node> bNodeTable = new HashMap<String, Node>();

    /**
     * Parse a JSON format stream into an RDF model.
     */
    public Model parse(InputStream in) {
        return modelFromJson( JSON.parse(in) );
    }

    /**
     * Parse a JSON format String into an RDF model.
     */
    public Model parse(String in) {
        return modelFromJson( JSON.parse(in) );
    }

    /**
     * Parse a JSON structure into a newly-allocated RDF model.
     * @param json the JSON structure to parse
     */
    public Model modelFromJson( JsonObject json) {
        Model result = ModelFactory.createDefaultModel();
        parseJson(json, result);
        return result;
    }

    /**
     * Parse a JSON structure into an RDF model.
     * @param json the JSON structure to parse
     * @param model the model into which to place the results
     */
    public void parseJson( JsonObject json, Model model) {
        parseJson(json, model.getGraph());
    }

    /**
     * Parse a JSON structure into an RDF graph..
     * @param json the JSON structure to parse
     * @param model the model into which to place the results
     */
    public void parseJson( JsonObject json, Graph graph) {
        for (String uri : json.keys()) {
            Node resource = uri.startsWith("_:") ? bNodeFor(uri) : NodeFactory.createURI(uri);
            JsonObject entity = json.get(uri).getAsObject();

            for (String prop : entity.keys()) {
                Node propNode = NodeFactory.createURI(prop);
                JsonArray values = entity.get(prop).getAsArray();
                for (Iterator<JsonValue> it = values.iterator(); it.hasNext();) {
                    Node valueNode = parseValue( it.next(), graph );
                    graph.add( new Triple(resource, propNode, valueNode) );
                }
            }
        }
    }

    protected Node parseValue(JsonValue value, Graph graph) {
        if (value.isNumber()) {
            return NodeFactory.createLiteral(value.toString(), XSDDatatype.XSDinteger);

        } else if (value.isString()) {
            return NodeFactory.createLiteral(value.getAsString().value());

        } else if (value.isObject()) {

            JsonObject valueDef = value.getAsObject();
            String type = getRequiredKey(valueDef, TYPE_KEY).getAsString().value();
            if (type.equals(URI_TYPE)) {
                return NodeFactory.createURI( getValueAsString(valueDef) );

            } else if (type.equals(BNODE_TYPE)) {
                return bNodeFor( getValueAsString(valueDef) );

            } else if (type.equals(LIST_TYPE)) {
                JsonArray listValues = valueDef.get(VALUE_KEY).getAsArray();
                Node head = null;
                Node prev = null;
                for (Iterator<JsonValue> i = listValues.iterator(); i.hasNext();) {
                    Node v = parseValue( i.next(), graph );
                    Node cell = NodeFactory.createBlankNode();
                    graph.add( new Triple(cell, RDF.first.asNode(), v) );
                    if (prev != null) {
                        graph.add( new Triple(prev, RDF.rest.asNode(), cell) );
                    }
                    prev = cell;
                    if (head == null) head = cell;
                }
                if (prev != null) {
                    graph.add( new Triple(prev, RDF.rest.asNode(), RDF.nil.asNode()) );
                }
                return head == null ? RDF.nil.asNode() : head;

            } else if (type.equals(LITERAL_TYPE)) {
                String lang = getValueAsString(valueDef, LANG_KEY);
                String dturi = getValueAsString(valueDef, DATATYPE_KEY);
                String lex = getValueAsString(valueDef);

                if (lang != null) {
                    return NodeFactory.createLiteral(lex, lang, false);
                }
                if (dturi != null) {
                    RDFDatatype dt = TypeMapper.getInstance().getSafeTypeByName(dturi);
                    return NodeFactory.createLiteral(lex, dt);
                }
                return NodeFactory.createLiteral(lex);

            } else {
                throw new RiotException("Property value type must be one of uri, bnode, literal or array. Found " + type);
            }

        } else {
            throw new RiotException("Property values can only be strings, numbers or structured objects");
        }

    }

    protected JsonValue getRequiredKey(JsonObject obj, String key) {
        JsonValue result = obj.get(key);
        if (result == null) {
            throw new RiotException("Didn't find expected key '" + key + "' on property value entry");
        }
        return result;
    }

    protected String getValueAsString(JsonObject obj) {
        String v = getValueAsString(obj, VALUE_KEY);
        if (v == null) {
            throw new RiotException("No 'value' property string on node value");
        }
        return v;
    }

    protected String getValueAsString(JsonObject obj, String key) {
        JsonValue v =  obj.get(key);
        if (v != null && v.isString()) {
            return v.getAsString().value();
        } else {
            return null;
        }
    }

    protected Node bNodeFor(String id) {
        Node valueNode = bNodeTable.get(id);
        if (valueNode == null) {
            valueNode = NodeFactory.createBlankNode();
            bNodeTable.put(id, valueNode);
        }
        return valueNode;
    }

}

