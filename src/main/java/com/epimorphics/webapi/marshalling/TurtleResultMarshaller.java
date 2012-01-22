/******************************************************************
 * File:        TurtleResultMarshaller.java
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

import com.epimorphics.webapi.ResultPage;
import com.hp.hpl.jena.util.FileUtils;


@Provider
@Produces("text/turtle; charset=UTF-8")
public class TurtleResultMarshaller implements MessageBodyWriter<ResultPage>{

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return type.equals( ResultPage.class );
    }

    @Override
    public long getSize(ResultPage t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(ResultPage t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException,
            WebApplicationException {
        t.getModel().write(entityStream, FileUtils.langTurtle);
    }



}

