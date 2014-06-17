/*                                                                                                                            
    LICENCE summary to go here.                                                                                        
    
    (c) Copyright 2014 Epimorphics Limited
*/
package com.epimorphics.json;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonBoolean;
import org.apache.jena.atlas.json.JsonNumber;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonString;
import org.apache.jena.atlas.json.JsonValue;
import org.junit.Test;

/**
    Added (some) tests when adding arrayElement(Number).
*/
public class TestJSONFullWriter {

	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	JSFullWriter jw = new JSFullWriter(bos);

	@Test public void testEmptyArray() {
	
		jw.startArray();
		jw.finishArray();
		jw.finishOutput();
		
		assertEquals( new JsonArray(), JSON.parseAny( bos.toString() ) );
	}

	@Test public void testSingletonStringArray() {
	
		jw.startArray();
		jw.arrayElement("hello");
		jw.finishArray();
		jw.finishOutput();
		
		assertEquals( array(string("hello")), JSON.parseAny( bos.toString() ) );
	}

	@Test public void testSingletonBooleanArray() {
	
		jw.startArray();
		jw.arrayElement(true);
		jw.finishArray();
		jw.finishOutput();
		
		assertEquals( array(bool(true)), JSON.parseAny( bos.toString() ) );
	}

	@Test public void testSingletonNumberArray() {
	
		jw.startArray();
		jw.arrayElement( new BigDecimal("17.17") );
		jw.finishArray();
		jw.finishOutput();
		
		assertEquals( array(decimal("17.17")), JSON.parseAny( bos.toString() ) );
	}

	@Test public void testSingletonLongArray() {
	
		jw.startArray();
		jw.arrayElement(17l);
		jw.finishArray();
		jw.finishOutput();
		
		assertEquals( array(a_long(17)), JSON.parseAny( bos.toString() ) );
	}
	
	@Test
	public void testNestedObjects() {
        JsonObject jo = new JsonObject();
        jo.put("string", "hello");
        jo.put("number", 42);
        jo.put("boolean", false);
        
        JsonArray array = new JsonArray();
        array.add(42);
        array.add("hello world");
        
        JsonObject jo2 = new JsonObject();
	    jo2.put("object", jo);
	    jo2.put("array", array);
	    
	    jw.startOutput();
	    jw.value(jo2);
	    jw.finishOutput();
	    
	    assertEquals( jo2, JSON.parse( bos.toString() ) );
	}
	
	JsonValue string(String s) {
		return new JsonString(s);
	}
	
	JsonValue bool(boolean b) {
		return new JsonBoolean(b);
	}
	
	JsonValue a_long(long l) {
		return JsonNumber.value(l);
	}
	
	JsonValue decimal(String number) {
		return JsonNumber.value(new BigDecimal(number));
	}
	
	JsonValue array(JsonValue ... elements ) {
		JsonArray result = new JsonArray();
		for (JsonValue jv: elements) result.add(jv);
		return result;
	}
}
