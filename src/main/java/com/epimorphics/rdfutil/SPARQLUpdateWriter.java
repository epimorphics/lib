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

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.shared.PrefixMapping;

import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.PrefixMapAdapter;
import org.apache.jena.riot.system.RiotLib;
import org.apache.jena.riot.writer.TurtleShell;
import org.apache.jena.riot.writer.TurtleWriterBase;
import org.apache.jena.sparql.util.Context;

/**
 * Support for writing out a model in SPARQL UPDATE syntax.
 * Provides separate writing of prefix and body information.
 *
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class SPARQLUpdateWriter {

    public SPARQLUpdateWriter() {
        // Constructor for compatibility
    }

    public static void writeUpdatePrefixes(PrefixMapping prefixes, Writer writer) throws IOException {
        for (Map.Entry<String, String> pm : prefixes.getNsPrefixMap().entrySet()) {
            writer.write("PREFIX " + pm.getKey() + ": <" + pm.getValue() + ">\n");
        }
    }

    private static class TurtleWriterNoPrefix extends TurtleWriterBase {

        @Override
        protected void output(IndentedWriter iOut, Graph graph, PrefixMap prefixMap, String baseURI, Context context) {
            TurtleWriter$ w = new TurtleWriter$(iOut, prefixMap, baseURI, context);
            w.write(graph);
        }

        private static class TurtleWriter$ extends TurtleShell {
            public TurtleWriter$(IndentedWriter out, PrefixMap prefixMap, String baseURI, Context context) {
                super(out, prefixMap, baseURI, context);
            }

            private void write(Graph graph) {
                writeGraphTTL(graph);
            }
        }
    }

    public void writeUpdateBody(Model model, Writer writer) throws IOException {
        TurtleWriterNoPrefix turtleWriter = new TurtleWriterNoPrefix();
        turtleWriter.output(
            RiotLib.create(writer),
            model.getGraph(),
            new PrefixMapAdapter(model),
            null,
            new Context()
        );
    }
}
