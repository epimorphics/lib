/******************************************************************
 * File:        JSONObjectMarshaller.java
 * Created by:  Dave Reynolds
 * Created on:  16 Dec 2011
 *
 * (c) Copyright 2011, Epimorphics Limited
 *****************************************************************/

package com.epimorphics.webapi.marshalling;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;

/**
 * The default Jersey Marshaller doesn't seem to get registered so
 * provide our own. Since we are doing that anyway then using the
 * Atlas JSON implementation.
 *
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
@Provider
@Produces("application/json")
public class JSONObjectMarshaller implements  MessageBodyWriter<JsonValue> {

    @Override
    public long getSize(JsonValue result, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return (type.equals(JsonArray.class) || type.equals(JsonObject.class) || type.equals(JsonValue.class));
    }

    @Override
    public void writeTo(JsonValue value, Class<?> doNotUse, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {

        IndentedWriter iw = new IndentedWriter( entityStream );
        value.output(iw);
        entityStream.write('\n');
    }

}

