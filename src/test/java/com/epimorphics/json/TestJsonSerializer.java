/******************************************************************
 * File:        TestJsonSerializer.java
 * Created by:  Dave Reynolds
 * Created on:  2 May 2014
 * 
 * (c) Copyright 2014, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.json;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestJsonSerializer {

    @Test
    public void testJsonSerializer() {
        // Create test data
        Map<String, Object> map0 = new HashMap<String, Object>();
        map0.put("key1", "value 0 - 1");
        
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("key1", "value 1");
        map1.put("key2", true);
        map1.put("key3", 42);
        map1.put("object", map0);
        
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("key1", "value 2 - 1");
        map2.put("key2", false);
        List<Object> listnest = new ArrayList<>();
        listnest.add(1);
        listnest.add(2);
        listnest.add(3);
        map2.put("list", listnest);
        
        List<Object> list = new ArrayList<>();
        list.add(map1);
        list.add(map2);
        
        // Seralize it
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JSFullWriter jw = new JSFullWriter(bos);
        jw.startOutput();
        new JSONSerializer(list).writeTo(jw);
        jw.finishOutput();
//        System.out.println( bos.toString() );
        
        // Parse back for testing
        JsonValue jv = JSON.parseAny( bos.toString() );
        assertTrue(jv.isArray());
        JsonArray ja = jv.getAsArray();
        assertEquals(2, ja.size());
        
        JsonObject jo1 = ja.get(0).getAsObject();
        assertEquals("value 1", jo1.get("key1").getAsString().value());
        assertEquals(42, jo1.get("key3").getAsNumber().value().intValue());
        assertEquals(true, jo1.get("key2").getAsBoolean().value());
        
        JsonObject jo0 = jo1.get("object").getAsObject();
        assertEquals("value 0 - 1", jo0.get("key1").getAsString().value());
        assertEquals("value 1", jo1.get("key1").getAsString().value());
        
        JsonObject jo2 = ja.get(1).getAsObject();
        assertEquals("value 2 - 1", jo2.get("key1").getAsString().value());
        assertEquals(false, jo2.get("key2").getAsBoolean().value());
        
        ja = jo2.get("list").getAsArray();
        assertEquals(3, ja.size());
        assertEquals(1, ja.get(0).getAsNumber().value().intValue());
        assertEquals(2, ja.get(1).getAsNumber().value().intValue());
        assertEquals(3, ja.get(2).getAsNumber().value().intValue());
    }
}
