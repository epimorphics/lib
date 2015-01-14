/******************************************************************
 * File:        DomWrapper.java
 * Created by:  Dave Reynolds
 * Created on:  7 Dec 2014
 * 
 * (c) Copyright 2014, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.xmlutil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.epimorphics.util.EpiException;

/**
 * Utility to create a DOM structure for an XML source and provide
 * XPath-based access and iteration over it.
 * <p>
 * Default is to not be namespace aware. If set to be namespace aware then
 * the namespace of the root node of the document will be used as the default namespace.
 * Additional namespace mappings can be registered with addnamespace.
 *
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class DomWrapper {

    boolean namespaceAware = false;
    SimpleNamespaceContext nsContext = new SimpleNamespaceContext();
    Document doc;
    XPath xpath;

    public DomWrapper()  {
    }
    
    /**
     * Set to true to make the parse and accesses namespace aware.
     */
    public void setNamespaceAware(boolean aware) {
        namespaceAware = aware;
    }
    
    /**
     * Register a namespace prefix
     */
    public void addNamespace(String prefix, String uri) {
        nsContext.addNamespace(prefix, uri);
    }
    
    /**
     * Load and parse a source document.
     */
    public void load(String file) {
        try {
            doc = getBuilder().parse(file);
            initXpath();
        } catch (Exception e) {
            throw new EpiException(e);
        }
    }
    
    /**
     * Load and parse a source document.
     */
    public void load(InputStream in) {
        try {
            doc = getBuilder().parse(in);
            initXpath();
        } catch (Exception e) {
            throw new EpiException(e);
        }
    }
    
    private DocumentBuilder getBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware( namespaceAware );
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        return builder;
    }
    
    private void initXpath() {
        XPathFactory factory = XPathFactory.newInstance();
        xpath = factory.newXPath();
        if (namespaceAware) {
            xpath.setNamespaceContext(nsContext);
            Node root = doc.getFirstChild();
            if (root != null) {
                String defaultNS = root.getNamespaceURI();
                if (defaultNS != null) {
                    nsContext.addNamespace("", defaultNS);
                }
            }
        }
    }

    /**
     * Return the document root node.
     */
    public Document getDocument() {
        return doc;
    }

    /**
     * Find the node addressed by the given path starting from the root of the document
     */
    public Node findNode(String path) {
        return findNode(path, doc);
    }

    /**
     * Find the node addressed by the given path starting from the given root
     */
    public Node findNode(String path, Node root) {
        try {
            XPathExpression expr = xpath.compile( path );
            return (Node) expr.evaluate(root, XPathConstants.NODE);
        } catch (Exception e) {
            throw new EpiException(e);
        }
    }

    /**
     * Find all nodes matching the given xpath expression, starting
     * from the root of the document
     */
    public List<Node> listNodes(String path) {
        return listNodes(path, doc);
    }

    /**
     * Find all nodes matching the given xpath expression
     * @param path the expression to search for
     * @param root the root node to search from
     * @return list of matching nodes
     */
    public List<Node> listNodes(String path, Node root) {
        try {
            XPathExpression expr = xpath.compile( path );
            NodeList result = (NodeList) expr.evaluate(root, XPathConstants.NODESET);
            List<Node> listr = new ArrayList<Node>();
            for (int i = 0; i < result.getLength(); i++) {
                listr.add( result.item(i) );
            }
            return listr;
        } catch (Exception e) {
            throw new EpiException(e);
        }
    }

    /**
     * Helper function. Retrieve an attribute value from a node.
     */
    public static String getAttribute(Node n, String name) {
        Node a = n.getAttributes().getNamedItem(name);
        if (a != null) {
            return a.getNodeValue();
        } else {
            throw new EpiException("Could not find attribute " + name + " on " + n);
        }
    }


    /**
     * Return the text value addressed by the given xpath starting from the given root.
     */
    public String textNode(String path, Node root) {
        try {
            XPathExpression expr = xpath.compile( path );
            return (String) expr.evaluate(root, XPathConstants.STRING);
        } catch (Exception e) {
            throw new EpiException(e);
        }
    }


    /**
     * Return the text value addressed by the given xpath starting from the given root.
     */
    public String textNodes(String path, Node root) {
        try {
            StringBuffer result = new StringBuffer();
            XPathExpression expr = xpath.compile( path );
            NodeList nodes = (NodeList) expr.evaluate(root, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node instanceof Text) {
                    result.append(((Text)node).getWholeText());
                }
            }
            return result.toString();
        } catch (Exception e) {
            throw new EpiException(e);
        }
    }

    /**
     * Return the text value addressed by the given xpath starting from the document root
     */
    public String textNode(String path) {
        return textNode(path, doc);
    }

    /**
     * Return a serialization of element/text contents of this node.
     */
    // There is probably a builtin utility for this somewhere ...
    public static String serialize(Node root) {
        StringBuffer buffer = new StringBuffer();
        serializeTo(root, buffer);
        return buffer.toString();
    }

    public static StringBuffer serializeTo(Node n, StringBuffer buffer) {
        switch ( n.getNodeType() ) {
        case Node.ELEMENT_NODE:
            serializeElement(n, buffer);
            break;

        case Node.TEXT_NODE:
        case Node.CDATA_SECTION_NODE:
            serializeText(n, buffer);
            break;

        case Node.PROCESSING_INSTRUCTION_NODE:
            serializePI(n, buffer);
            break;
        }
        return buffer;
    }

    private static void serializeElement(Node n, StringBuffer buffer) {
        buffer.append("<" + n.getNodeName() + " ");
        serializeAttributes(n, buffer);
        buffer.append(">");
        serializeChildren(n, buffer);
        buffer.append("</" + n.getNodeName() + ">");
    }

    private static void serializePI(Node n, StringBuffer buffer) {
        buffer.append("<?" + n.getNodeName() + " " + n.getNodeValue());
        buffer.append("?>");
    }


    public static StringBuffer serializeChildren(Node root, StringBuffer buffer) {
        NodeList nl = root.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            serializeTo(n, buffer);
        }
        return buffer;
    }

    private static void serializeAttributes(Node n, StringBuffer buffer) {
        NamedNodeMap nnm = n.getAttributes();
        for (int i = 0; i < nnm.getLength(); i++) {
            Node a = nnm.item(i);
            buffer.append(a.getNodeName() + "=\"" + a.getNodeValue() + "\" ");
        }
    }

    private static void serializeText(Node n, StringBuffer buffer) {
        buffer.append( StringEscapeUtils.escapeHtml( n.getNodeValue() ) );
    }

    /**
     * Return textual serialization of the text content of the node, stripping
     * out nested elements. For example:
     * <pre>
     *    &lt;emphasis>g&lt;/emphasis> general intelligence
     * </pre>
     * would return "g general intelligence"
     */
    public static String flatten(Node n) {
        StringBuffer buffer = new StringBuffer();
        flattenTo(n, buffer);
        return buffer.toString();
    }

    public static StringBuffer flattenTo(Node n, StringBuffer buffer) {
        switch ( n.getNodeType() ) {
        case Node.ELEMENT_NODE:
            flattenElement(n, buffer);
            break;

        case Node.TEXT_NODE:
        case Node.CDATA_SECTION_NODE:
            buffer.append( n.getNodeValue() );
            break;

        case Node.PROCESSING_INSTRUCTION_NODE:
            if (n.getNodeName().equals("inst")) {
                // special case used for emphasis in some of the markup
                buffer.append(n.getNodeValue() + " ");
            }
            break;
        }
        return buffer;
    }

    private static void flattenElement(Node root, StringBuffer buffer) {
        NodeList nl = root.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            flattenTo(n, buffer);
        }
    }

}
