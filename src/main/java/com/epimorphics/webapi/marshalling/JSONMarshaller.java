/******************************************************************
 * File:        JSONMarshaller.java
 * Created by:  Dave Reynolds
 * Created on:  13 Dec 2011
 *
 * (c) Copyright 2011, Epimorphics Limited
 *****************************************************************/

package com.epimorphics.webapi.marshalling;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.epimorphics.json.RDFJSONModWriter;
import com.hp.hpl.jena.rdf.model.Model;

@Provider
@Produces("application/json")
public class JSONMarshaller implements MessageBodyWriter<Model> {

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
        RDFJSONModWriter writer = new RDFJSONModWriter(entityStream);
        writer.write(t);
        writer.finish();
    }



}

