/******************************************************************
 * File:        JSONObjectMarshaller.java
 * Created by:  Dave Reynolds
 * Created on:  16 Dec 2011
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
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;

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

