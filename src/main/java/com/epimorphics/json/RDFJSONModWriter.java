/******************************************************************
 * File:        RDFJSONModWriter.java
 * Created by:  Dave Reynolds
 * Created on:  13 Dec 2011
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

import static com.epimorphics.json.RDFJSONModReader.BNODE_TYPE;
import static com.epimorphics.json.RDFJSONModReader.DATATYPE_KEY;
import static com.epimorphics.json.RDFJSONModReader.LANG_KEY;
import static com.epimorphics.json.RDFJSONModReader.LIST_TYPE;
import static com.epimorphics.json.RDFJSONModReader.LITERAL_TYPE;
import static com.epimorphics.json.RDFJSONModReader.TYPE_KEY;
import static com.epimorphics.json.RDFJSONModReader.URI_TYPE;
import static com.epimorphics.json.RDFJSONModReader.VALUE_KEY;

import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.atlas.json.io.JSWriter;
import org.apache.jena.riot.RiotException;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.impl.XSDBaseNumericType;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphUtil;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.util.graph.GNode;
import org.apache.jena.sparql.util.graph.GraphList;
import org.apache.jena.util.OneToManyMap;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;

/**
 * <p>Support for serializing sets of RDF resources and whole models in JSON.</p>
 *
 * <p>In lieu of the W3C standard we are mostly following the Talis
 * <a href="http://docs.api.talis.com/platform-api/output-types/rdf-json">format</a>.
 * Unfortunately that doesn't cater for lists in a usable fashion and our primary
 * use case is outputting lists of results. So we extend the Talis format with
 * a value type "list" which is rendered in JSON as an array.</p>
 *
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */

// There is a Talis format reader/writer in ARQ/RIOT that that seems fairly hardwired
// to the Talis format at is doesn't look easy to extend it to do the Array support.

public class RDFJSONModWriter {

    // Tracks resources that have be written out manually (or as part of a nest recursion)
    // and should not be included in a whole model scan.
    protected Set<Node> visited = new HashSet<Node>();

    protected JSWriter writer;

    public RDFJSONModWriter(OutputStream out) {
        writer = new JSWriter(out);
        start();
    }

//    public RDFJSONModWriter(Writer out) {
//        writer = new JSWriter( new IndentedW);
//        start();
//    }

    protected void start() {
        writer.startOutput();
        writer.startObject();
    }

    /**
     * Write a single resource
     */
    public void writeResource(Resource resource) {
        writeResource(resource.getModel().getGraph(), resource.asNode(), true);
    }

    /**
     * Write a single graph node
     * @param graph  the graph from which to write
     * @param node   the specific node to write out properties for
     * @param recordVisit if true then this visit will be record to be blocked from an subsequent full write
     */
    public void writeResource(Graph graph, Node node, boolean recordVisit) {
        if (recordVisit) {
            visited.add(node);
        }
        writer.key(nodeLabel(node));
        writer.startObject();

        ExtendedIterator<Triple> it = graph.find(node, null, null);
        OneToManyMap<Node, Node> props = new OneToManyMap<Node, Node>();
        while (it.hasNext()) {
            Triple t = it.next();
            props.put(t.getPredicate(), t.getObject());
        }

        for (Node prop : props.keySet()) {
            writer.key(prop.getURI());
            writer.startArray();
            boolean isFirst = true;
            for (Iterator<Node> ni =  props.getAll(prop); ni.hasNext(); ) {
                writeNodeValue(graph, ni.next(), isFirst);
                isFirst = false;
            }
            writer.finishArray();
        }
        writer.finishObject();
    }

    protected void writeNodeValue(Graph graph, Node node, boolean isFirst) {
        if (node.isLiteral()) {
            writeLiteral(node.getLiteral(), isFirst);
        } else {
            if (!isFirst) {
                writer.arraySep();
            }
            writer.startObject();
            if (isListNode(graph, node)) {
                writeKeyValue(TYPE_KEY, LIST_TYPE);
                writer.key(VALUE_KEY);
                writer.startArray();
                boolean first = true;
                for (Node listnode : GraphList.members(new GNode(graph, node))) {
                    writeNodeValue(graph, listnode, first);
                    first = false;
                }
                writer.finishArray();
            } else if (node.isURI()) {
                writeKeyValue(TYPE_KEY, URI_TYPE);
                writeKeyValue(VALUE_KEY, node.getURI());
            } else if (node.isBlank()) {
                writeKeyValue(TYPE_KEY, BNODE_TYPE);
                writeKeyValue(VALUE_KEY, nodeLabel(node));
            }
            writer.finishObject();
        }
    }

    protected void writeLiteral(LiteralLabel literal, boolean isFirst) {
        RDFDatatype dt = literal.getDatatype();
        if (dt != null) {
            if (dt instanceof XSDBaseNumericType
                    && !dt.equals(XSDDatatype.XSDfloat)
                    && !dt.equals(XSDDatatype.XSDdouble)
                    && !dt.equals(XSDDatatype.XSDdecimal)) {
                // arrayElement does it's own insertion of separators
                writer.arrayElement( Long.parseLong( literal.getLexicalForm() ) );
                return;
            }
        }
        if (!isFirst) {
            writer.arraySep();
        }
        writer.startObject();
        writeKeyValue( TYPE_KEY, LITERAL_TYPE );
        if (dt != null) {
            writeKeyValue( DATATYPE_KEY, dt.getURI() );
        }
        String lang = literal.language();
        if (lang != null && !lang.isEmpty()) {
            writeKeyValue( LANG_KEY, lang );
        }
        writeKeyValue( VALUE_KEY, literal.getLexicalForm() );
        writer.finishObject();
    }

    protected boolean isListNode(Graph g, Node n) {
        return n.equals(RDF.nil.asNode()) || (n.isBlank() && (g.contains(n, RDF.first.asNode(), Node.ANY) && g.contains(n, RDF.rest.asNode(), Node.ANY)));
    }

    protected void writeKeyValue(String key, String value) {
        writer.pair(key, value);
    }

    protected void writeKeyValue(String key, long value) {
        writer.pair(key, value);
    }

    protected String nodeLabel(Node node) {
        if (node.isURI()) {
            return node.getURI();
        } else if (node.isBlank()){
            return "_:" + node.getBlankNodeLabel();
        } else {
            throw new RiotException("Internal error - found non-resource node");
        }
    }

    /**
     * Write the given model, not including a resources that have already been visited
     */
    public void write(Model model) {
        write(model.getGraph());
    }

    /**
     * Write the given graph, not including a resources that have already been visited
     */
    public void write(Graph graph) {
        ExtendedIterator<Node> ni = GraphUtil.listSubjects(graph, null, null);
        while (ni.hasNext()) {
            Node n = ni.next();
            if (!visited.contains(n)) {
                if ( ! isListNode(graph, n)) {
                    writeResource(graph, n, false);
                }
            }
        }
    }

    /**
     * Write the given ResultPage out with a wrapper object round the graph data
     * showing the root, page and results list.
     */
    public void write(Resource root, Resource page, Iterable<Resource> items) {
        writeKeyValue("root", root.getURI());
        writeKeyValue("page", page.getURI());

        writer.key("items");
        writer.startArray();
        for (Resource item : items) {
            writer.arrayElement( item.getURI() );
        }
        writer.finishArray();

        writer.key("graph");
        writer.startObject();
        writeResource( root );
        writeResource( page);
        for (Resource r : items) {
            writeResource( r );
        }

        // write the rest of the model as well, replies on visited track to avoid duplication
        write( root.getModel() );

        writer.finishObject();
    }

    /**
     * Finish the write. Not called "close" because it doesn't not close the underlying
     * output stream.
     */
    public void finish() {
        writer.finishObject();
        writer.finishOutput();
    }
}

