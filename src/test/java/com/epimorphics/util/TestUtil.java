/******************************************************************
 * File:        TestUtil.java
 * Created by:  Dave Reynolds
 * Created on:  30 Nov 2011
 *
 * (c) Copyright 2011, Epimorphics Limited
 *****************************************************************/

package com.epimorphics.util;

import static junit.framework.Assert.*;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.*;

/**
 * Support for testing iterator/list values against and expected set
 * of answers.
 *
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class TestUtil {

    public static <E> void testArray(E[] actual, E[] expected) {
        Set<Object> expectedSet = new HashSet<Object>();
        for (Object e : expected) expectedSet.add(e);

        Set<Object> actualSet = new HashSet<Object>();
        for (Object a : actual) actualSet.add(a);

        assertEquals(expectedSet, actualSet);
    }

    public static <E> void testArray(List<E> actual, E[] expected) {
        Set<Object> expectedSet = new HashSet<Object>();
        for (Object e : expected) expectedSet.add(e);

        Set<Object> actualSet = new HashSet<Object>( actual );

        assertEquals(expectedSet, actualSet);
    }

    public static <E> void testArray(Iterator<E> actual, E[] expected) {
        Set<Object> expectedSet = new HashSet<Object>();
        for (Object e : expected) expectedSet.add(e);

        Set<Object> actualSet = new HashSet<Object>( );
        while (actual.hasNext()) {
            actualSet.add(actual.next());
        }

        assertEquals(expectedSet, actualSet);
    }

    /**
     * Create a {@link Model} as a text fixture
     * @param content The model content in Turtle. Common prefixes may be assumed.
     * @return A new model containing triples parsed from the content
     */
    public static Model modelFixture( String content ) {
        Model m = ModelFactory.createDefaultModel();
        m.setNsPrefixes( PrefixUtils.commonPrefixes() );
        m.setNsPrefix( "", baseURIFixture() );
        String withPrefixes = PrefixUtils.asTurtlePrefixes( m ) + content;
        m.read( new StringReader( withPrefixes ), baseURIFixture(), "Turtle" );
        return m;
    }

    /**
     * Return a resource with the given URI.
     * @param m Optional model. If null, the resource will be created using the {@link ResourceFactory}
     * @param uri Resource URI. If the URI starts with <code>http:</code>, it will be left intact otherwise
     * it is assumed relative to the {@link #baseURIFixture()}
     * @return A resource
     */
    public static Resource resourceFixture( Model m, String uri ) {
        String u = uri.startsWith( "http:" ) ? uri : (baseURIFixture() + uri);
        return (m == null) ? ResourceFactory.createResource( u ) : m.getResource( u );
    }

    /**
     * Return a property with the given URI.
     * @param m Optional model. If null, the property will be created using the {@link ResourceFactory}
     * @param uri Resource URI. If the URI starts with <code>http:</code>, it will be left intact otherwise
     * it is assumed relative to the {@link #baseURIFixture()}
     * @return A property
     */
    public static Resource propertyFixture( Model m, String uri ) {
        String u = uri.startsWith( "http:" ) ? uri : (baseURIFixture() + uri);
        return (m == null) ? ResourceFactory.createProperty( u ) : m.getProperty( u );
    }

    /**
     * Return a base URI that is guaranteed not to resolve.
     * @return "http://example.test/test#"
     */
    public static String baseURIFixture() {
        return "http://example.test/test#";
    }
}

