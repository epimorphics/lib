/******************************************************************
 * File:        TestUtil.java
 * Created by:  Dave Reynolds
 * Created on:  30 Nov 2011
 *
 * (c) Copyright 2011, Epimorphics Limited
 *****************************************************************/

package com.epimorphics.util;

import static junit.framework.Assert.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

}

