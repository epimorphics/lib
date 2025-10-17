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

import org.apache.jena.rdf.model.Model;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriter;
import java.nio.charset.StandardCharsets;

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

    public void writeUpdateBody(Model model, Writer writer) throws IOException {
        // Use modern Jena RIOT writer to write in Turtle format
        // which is suitable for SPARQL UPDATE body
        // Convert Writer to OutputStream since RIOT uses OutputStream
        try (java.io.PipedOutputStream pos = new java.io.PipedOutputStream();
             java.io.PipedInputStream pis = new java.io.PipedInputStream(pos)) {
            
            // Start a separate thread to write to the pipe
            Thread writerThread = new Thread(() -> {
                try {
                    RDFWriter.create()
                        .format(RDFFormat.TTL)
                        .source(model)
                        .output(pos);
                } finally {
                    try {
                        pos.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            });
            writerThread.start();
            
            // Read from pipe and write to the output writer
            try (java.io.InputStreamReader reader = new java.io.InputStreamReader(pis, StandardCharsets.UTF_8)) {
                char[] buffer = new char[8192];
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            }
            
            writerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while writing model", e);
        }
    }

}
