/******************************************************************
 * File:        Testjava
 * Created by:  Dave Reynolds
 * Created on:  8 May 2014
 * 
 * (c) Copyright 2014, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonBoolean;
import org.apache.jena.atlas.json.JsonNumber;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonString;
import org.apache.jena.atlas.json.JsonValue;
import org.junit.Test;

import static com.epimorphics.json.JsonUtil.*;

import static org.junit.Assert.*;

public class TestJsonUtil {

    @Test
    public void testAsJson() {
        assertEquals( asJson("foo bar"), new JsonString("foo bar") );
        assertEquals( asJson(true), new JsonBoolean(true) );
        assertEquals( asJson(42), JsonNumber.value(42) );
        assertEquals( asJson(42.3), JsonNumber.value(42.3) );
        assertEquals( asJson((long)42), JsonNumber.value(42) );
        assertEquals( asJson(new BigDecimal("12345678913456789.987654321")), JsonNumber.valueDecimal("12345678913456789.987654321") );
        
        List<Object> list = new ArrayList<>();
        list.add( "foo" );
        list.add( 42 );
        list.add( false );
        JsonArray ja = asJson(list).getAsArray();
        assertEquals( ja.get(0), new JsonString("foo"));
        assertEquals( ja.get(1), JsonNumber.value(42));
        assertEquals( ja.get(2), new JsonBoolean(false));
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("string", "foo bar");
        map.put("number", 42);
        map.put("list", list);
        JsonObject jo = asJson(map).getAsObject();
        assertEquals( jo.get("string"), new JsonString("foo bar") );
        assertEquals( jo.get("number"), JsonNumber.value(42) );
        assertEquals( jo.get("list"), ja);
        
        JsonObject mjo = makeJson("string", "foo bar", "number", 42, "list", list);
        assertEquals(jo, mjo);
        
        assertEquals("foo bar", getPath(mjo, "string"));
        assertEquals("foo bar", getPath(mjo, String.class, "string"));
        assertEquals("foo", getPath(mjo, "list", 0));
        assertEquals(false, getPath(mjo, "list", 2));
    }
    
    @Test 
    public void testFromJson() {
        testAsFrom( "foo bar" );
        testAsFrom( true );
        testAsFrom( (long)42 );
        testAsFrom( new BigDecimal("12345678913456789.987654321") );
        testAsFrom( null );
        
        List<Object> list = new ArrayList<>();
        list.add( "foo" );
        list.add( (long) 42 );
        list.add( false );
        testAsFrom( list );
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("string", "foo bar");
        map.put("number", (long)42);
        map.put("list", list);
        testAsFrom( map );
    }
    
    private void testAsFrom(Object arg) {
        JsonValue j = asJson(arg);
        Object o = fromJson(j);
        assertEquals(arg, o);
    }
    
    @Test
    public void testMerge() {
        JsonObject base = makeJson("a", "foo", "b", 42, "c", false);
        JsonObject extend = makeJson("c", true, "d", "marvellous");
        JsonObject expected = makeJson("a", "foo", "b", 42, "c", true, "d", "marvellous");
        assertEquals(expected, merge(base, extend));
    }
    
}
