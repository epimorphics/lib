/******************************************************************
 * File:        RDFJSONModWriter.java
 * Created by:  Dave Reynolds
 * Created on:  13 Dec 2011
 *
 * (c) Copyright 2011, Epimorphics Limited
 *****************************************************************/

package com.epimorphics.json;

import java.io.OutputStream;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.openjena.riot.RiotException;

import com.epimorphics.jsonrdf.extras.JSStreamingWriter;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.datatypes.xsd.impl.XSDBaseNumericType;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.util.graph.GNode;
import com.hp.hpl.jena.sparql.util.graph.GraphList;
import com.hp.hpl.jena.util.OneToManyMap;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import static com.epimorphics.json.RDFJSONModReader.*;

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

    protected JSStreamingWriter writer;

    public RDFJSONModWriter(OutputStream out) {
        writer = new JSStreamingWriter(out);
        start();
    }

    public RDFJSONModWriter(Writer out) {
        writer = new JSStreamingWriter(out);
        start();
    }

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
            for (Iterator<Node> ni =  props.getAll(prop); ni.hasNext(); ) {
                writeNodeValue(graph, ni.next());
            }
            writer.finishArray();
        }
        writer.finishObject();
    }

    protected void writeNodeValue(Graph graph, Node node) {
        if (node.isLiteral()) {
            writeLiteral(node.getLiteral());
        } else {
            writer.startObject();
            if (isListNode(graph, node)) {
                writeKeyValue(TYPE_KEY, LIST_TYPE);
                writer.key(VALUE_KEY);
                writer.startArray();
                for (Node listnode : GraphList.members(new GNode(graph, node))) {
                    writeNodeValue(graph, listnode);
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

    protected void writeLiteral(LiteralLabel literal) {
        RDFDatatype dt = literal.getDatatype();
        if (dt != null) {
            if (dt instanceof XSDBaseNumericType
                    && !dt.equals(XSDDatatype.XSDfloat)
                    && !dt.equals(XSDDatatype.XSDdouble)
                    && !dt.equals(XSDDatatype.XSDdecimal)) {
                writer.value( Long.parseLong( literal.getLexicalForm() ) );
                return;
            }
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
        writer.key(key);
        writer.value(value);
    }

    protected void writeKeyValue(String key, long value) {
        writer.key(key);
        writer.value(value);
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
        ExtendedIterator<Node> ni = graph.queryHandler().subjectsFor(null, null);
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
            writer.value( item.getURI() );
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

