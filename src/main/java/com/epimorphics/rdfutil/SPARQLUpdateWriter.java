/******************************************************************
 * File:        SPARQLUpdateWriter.java
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.hp.hpl.jena.n3.N3IndentedWriter;
import com.hp.hpl.jena.n3.N3JenaWriterCommon;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.PrefixMapping;

/**
 * Support for writing out a model in SPARQL UPDATE syntax.
 * Essentially just the normal N3/Turle writer but with
 * support for separate writing of prefix and body information.
 *
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class SPARQLUpdateWriter extends N3JenaWriterCommon {

    public SPARQLUpdateWriter() {
        useWellKnownPropertySymbols = false;
    }

    public static void writeUpdatePrefixes(PrefixMapping prefixes, Writer writer) throws IOException {
        for (Map.Entry<String, String> pm : prefixes.getNsPrefixMap().entrySet()) {
            writer.write("PREFIX " + pm.getKey() + ": <" + pm.getValue() + ">\n");
        }
    }

    public void writeUpdateBody(Model model, Writer _out) throws IOException {
        // Set up output
        if (!(_out instanceof BufferedWriter)) {
            _out = new BufferedWriter(_out);
        }
        out = new N3IndentedWriter(_out);

        bNodesMap = new HashMap<Resource, String>() ;

        // Set up prefix mapping
        prefixMap = model.getNsPrefixMap() ;
        for ( Iterator<Entry<String, String>> iter = prefixMap.entrySet().iterator() ; iter.hasNext() ; )
        {
            Entry<String, String> e = iter.next() ;
            String prefix = e.getKey() ;
            String uri = e.getValue();

            // XML namespaces name can include '.'
            // Turtle prefixed names can't.
            if ( ! checkPrefixPart(prefix) )
                iter.remove() ;
            else
            {
                if ( checkPrefixPart(prefix) )
                    // Build acceptable reverse mapping
                    reversePrefixMap.put(uri, prefix) ;
            }
        }

//        writeModel( ModelFactory.withHiddenStatements(model));
        writeModel( model );
        out.flush();
        bNodesMap = null ;
    }

}
