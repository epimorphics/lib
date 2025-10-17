/******************************************************************
 * File:        RDFXMLMarshaller.java
 * Created by:  Dave Reynolds
 * Created on:  8 Dec 2011
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

package com.epimorphics.webapi.marshalling;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriter;

@Provider
@Produces("application/rdf+xml")
public class RDFXMLMarshaller implements MessageBodyWriter<Model>{

    public static final String MIME_RDFXML = "application/rdf+xml";
    public static final String FULL_MIME_RDFXML = "application/rdf+xml; charset=UTF-8";

    private static boolean useAbbreviatedWriter = true;

    public static boolean isUseAbbreviatedWriter() {
        return useAbbreviatedWriter;
    }

    public static void setUseAbbreviatedWriter(boolean useAbbreviatedWriter) {
        RDFXMLMarshaller.useAbbreviatedWriter = useAbbreviatedWriter;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        Class<?>[] sigs = type.getInterfaces();
        for (Class<?> sig: sigs) {
            if (sig.equals(Model.class)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public long getSize(Model t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Model t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException,
            WebApplicationException {
        RDFFormat format = useAbbreviatedWriter ? RDFFormat.RDFXML_ABBREV : RDFFormat.RDFXML_PLAIN;
        RDFWriter.create()
            .format(format)
            .source(t)
            .output(entityStream);
    }

}

