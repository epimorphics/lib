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
     * Return a resource which is the (assumed sole) root subject of the model given by the turtle source.
     * Common prefixes are assumed.
     */
    public static Resource resourceFixture( String src ) {
        Model m = modelFixture(src);
        for (ResIterator ri = m.listSubjects(); ri.hasNext();) {
            Resource root = ri.next();
            if (m.listStatements(null, null, root).hasNext()) {
                continue;
            } else {
                return root;
            }
        }
        return null;
    }

    /**
     * Return a property with the given URI.
     * @param m Optional model. If null, the property will be created using the {@link ResourceFactory}
     * @param uri Resource URI. If the URI starts with <code>http:</code>, it will be left intact otherwise
     * it is assumed relative to the {@link #baseURIFixture()}
     * @return A property
     */
    public static Property propertyFixture( Model m, String uri ) {
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

    /**
     * Compare the properties of two resources, omitting any of the list of block properties.
     * bNode values are ignored.
     */
    public static boolean resourcesMatch(Resource expected, Resource actual, Property... omit) {
        return oneWayMatch(true, expected, actual, omit) && oneWayMatch(false, actual, expected, omit);
    }

    private static boolean oneWayMatch(boolean forward, Resource expected, Resource actual,
            Property... omit) {
        for (StmtIterator si = expected.listProperties(); si.hasNext();) {
            Statement s = si.next();
            Property p = s.getPredicate();
            if (!blocked(p, omit)) {
                Statement a_s = actual.getProperty(p);
                if (a_s == null) {
                    if (forward) {
                        System.out.println("Expected property " + p + " missing");
                    } else {
                        System.out.println("Unexpected property " + p);
                    }
                    return false;
                }
                RDFNode a_value = a_s.getObject();
                RDFNode e_value = s.getObject();
                if (!e_value.isAnon() && !a_value.equals(e_value)) {
                    if (forward) {
                        System.out.println("Expected " + e_value + " but found " + a_value + ", on property " + p);
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean blocked(Property p, Property...omit) {
        for (Property o : omit) {
            if (o.equals(p)) {
                return true;
            }
        }
        return false;
    }
}

