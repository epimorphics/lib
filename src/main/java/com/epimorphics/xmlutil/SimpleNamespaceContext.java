/******************************************************************
 * File:        SimpleNamespaceContext.java
 * Created by:  Dave Reynolds
 * Created on:  7 Dec 2014
 * 
 * (c) Copyright 2014, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.xmlutil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

/**
 * Simple implementation of Namespace context,
 */
public class SimpleNamespaceContext implements NamespaceContext {
    Map<String, String> prefixToNs = new HashMap<String, String>();
    Map<String, String> nsToPrefix = new HashMap<String, String>();
    
    public SimpleNamespaceContext() {
    }
    
    public SimpleNamespaceContext(String prefix, String uri) {
        addNamespace(prefix, uri);
    }
    
    public void addNamespace(String prefix, String uri) {
        prefixToNs.put(prefix, uri);
        nsToPrefix.put(uri, prefix);
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return prefixToNs.get(prefix);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        return nsToPrefix.get(namespaceURI);
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        ArrayList<String> prefixes = new ArrayList<>();
        for (Map.Entry<String, String> entry : prefixToNs.entrySet()) {
            if (entry.getValue().equals(namespaceURI)) {
                prefixes.add(entry.getKey());
            }
        }
        return prefixes.iterator();
    }

}
