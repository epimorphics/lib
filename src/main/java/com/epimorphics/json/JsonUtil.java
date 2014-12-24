/******************************************************************
 * File:        JsonUtil.java
 * Created by:  Dave Reynolds
 * Created on:  16 Oct 2013
 * 
 * (c) Copyright 2013, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.json;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonBoolean;
import org.apache.jena.atlas.json.JsonNull;
import org.apache.jena.atlas.json.JsonNumber;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonString;
import org.apache.jena.atlas.json.JsonValue;
import org.yaml.snakeyaml.Yaml;

import com.epimorphics.util.EpiException;

/**
 * Utilities to make it easier to access JSON data and convert between JSON encodings
 * and plain java encodings.
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class JsonUtil {

    public static String getStringValue(JsonObject jo, String field) {
        return getStringValue(jo, field, null);
    }
    
    public static String getStringValue(JsonObject jo, String field, String dflt) {
        JsonValue val = jo.get(field);
        if (val == null || !val.isString()) {
            return dflt;
        } else {
            return val.getAsString().value();
        }
    }

    public static int getIntValue(JsonObject jo, String field, int dflt) {
        JsonValue val = jo.get(field);
        if (val == null || !val.isNumber()) {
            return dflt;
        } else {
            return val.getAsNumber().value().intValue();
        }
    }

    public static long getLongValue(JsonObject jo, String field, long dflt) {
        JsonValue val = jo.get(field);
        if (val == null || !val.isNumber()) {
            return dflt;
        } else {
            return val.getAsNumber().value().longValue();
        }
    }

    public static boolean getBooleanValue(JsonObject jo, String field, boolean dflt) {
        JsonValue val = jo.get(field);
        if (val == null || !val.isBoolean()) {
            return dflt;
        } else {
            return val.getAsBoolean().value();
        }
    }

    /**
     * Convert java objects to JSON objects. Handles strings, numbers, 
     * booleans, maps and lists and simple nests thereof.
     * Throws runtime exception if this is not possible.
     */
    public static JsonValue asJson(Object obj) {
        if (obj instanceof String) {
            return new JsonString((String)obj);
        } else if (obj instanceof Number) {
            if (obj instanceof Integer) {
                return JsonNumber.value( (Integer) obj);
            } else if (obj instanceof Long) {
                return JsonNumber.value( (Long) obj);
            } else if (obj instanceof Double) {
                return JsonNumber.value( (Double) obj);
            } else if (obj instanceof BigDecimal) {
                return JsonNumber.value( (BigDecimal) obj);
            } else if (obj instanceof BigInteger) {
                return JsonNumber.value( new BigDecimal( (BigInteger)obj ) );
            } else {
                return JsonNumber.valueDecimal(obj.toString());
            }
            
        } else if (obj instanceof Boolean){
            return new JsonBoolean((Boolean)obj);
            
        } else if (obj instanceof List<?>) {
            JsonArray a = new JsonArray();
            for (Object o : (List<?>)obj) {
                a.add( asJson(o) );
            }
            return a;
            
        } else if (obj instanceof Object[]) {
            JsonArray a = new JsonArray();
            for (Object o : (Object[])obj) {
                a.add( asJson(o) );
            }
            return a;
            
        } else if (obj instanceof Map<?, ?>) {
            JsonObject r = new JsonObject();
            for (Entry<?,?> entry : ((Map<?,?>)obj).entrySet()) {
                r.put( entry.getKey().toString(), asJson(entry.getValue()) );
            }
            return r;
            
        } else if (obj instanceof JsonValue) {
            return (JsonValue) obj;
            
        } else if (obj == null) {
            return JsonNull.instance;
            
        } else {
            throw new EpiException("Could not convert to JSON object: " + obj);
        }
    }
    
    /**
     * Create a simple Json Object.
     * @param args alternating parameter names and parameter values
     */
    public static JsonObject makeJson(Object...args) {
        return makeJson(null, args);
    }
    
    /**
     * Create a JSON object as a copy of an existing object, with new/replacement key values
     * taken from the args
     * @param base object to copy, can be null 
     * @param args alternating parameter names and parameter values
     */
    public static JsonObject makeJson(JsonObject base, Object...args) {
        if (args.length % 2 != 0) {
            throw new EpiException("makeJson requires an even number of arguments");
        }
        JsonObject result = new JsonObject();
        if (base != null) {
            for (Entry<String, JsonValue> e : base.entrySet()) {
                result.put(e.getKey(), e.getValue());
            }
        }
        for (int i = 0; i < args.length; ) {
            Object paramname = args[i++];
            Object paramval = args[i++];
            result.put(paramname.toString(), asJson(paramval));
        }
        return result;
    }
    
    /**
     * Convert a JSON value to a corresponding plain Java value.
     * A near inverse of asJson except that number types might be changed.
     */
    public static Object fromJson(JsonValue jv) {
        if (jv == null) return null;
        if (jv.isString()) {
            return jv.getAsString().value();
        } else if (jv.isBoolean()) {
            return jv.getAsBoolean().value();
        } else if (jv.isNumber()) {
            Object n = jv.getAsNumber().value();
            if (n instanceof BigDecimal) {
                try {
                    BigInteger i = ((BigDecimal)n).toBigIntegerExact();
                    if (i.bitLength() > 63) {
                        return i;
                    } else {
                        return i.longValue();
                    }
                } catch (ArithmeticException e) {
                    return n;
                }
            } else {
                return n;
            }
        } else if (jv.isObject()) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (Entry<String,JsonValue> entry : jv.getAsObject().entrySet()) {
                map.put(entry.getKey(), fromJson(entry.getValue()));
            }
            return map;
        } else if (jv.isArray()) {
            List<Object> list = new ArrayList<>( jv.getAsArray().size() );
            for (Iterator<JsonValue> i = jv.getAsArray().iterator(); i.hasNext();) {
                list.add( fromJson( i.next() ) );
            }
            return list;
        } else {
            return null;
        }
    }
    
    /**
     * Return an element of a JSON structure by following a sequence of object field
     * selectors (strings) or array indexes (integers) and return the java-ized
     * version of the leaf. Throws runtime exception if the structure does not match.
     */
    public static Object getPath(JsonValue root, Object...selectors) {
        JsonValue jv = root;
        String sofar = "$";
        for (Object selector : selectors) {
            if (selector instanceof String) {
                if (jv.isObject()) {
                    jv = jv.getAsObject().get((String)selector);
                } else {
                    throw new EpiException("Value at path " + sofar + " is not an object");
                }
            } else if (selector instanceof Number) {
                if (jv.isArray()) {
                    try {
                        jv = jv.getAsArray().get( ((Number)selector).intValue() );
                    } catch (IndexOutOfBoundsException e) {
                        throw new EpiException("Array at path " + sofar + " does not have value for " + selector);
                    }
                } else {
                    throw new EpiException("Value at path " + sofar + " is not an array");
                }
            } else {
                throw new EpiException("Path elements must be strings or integers, found: " + selector);
            }
            if (jv == null) {
                throw new EpiException("No value at path " + sofar + "." + selector);
            }
            sofar += "." + selector;
        }
        return fromJson(jv);
    }
    
    /**
     * Return an element of a JSON structure by following a sequence of object field
     * selectors (strings) or array indexes (integers) and return the java-ized
     * version of the leaf. Returns the value as the requested target class if possible. 
     * Throws runtime exception if the structure does not match.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getPath(JsonValue root, Class<T> target, Object...selectors) {
        Object val = getPath(root, selectors);
        if (target.isInstance(val)) {
            return (T)val;
        } else {
            throw new EpiException("Retrieved value " + val + " is not a " + target);
        }
    }

    /**
     * Singleton empty object. Risk of mutation!
     */
//    public static JsonObject EMPTY_OBJECT = new JsonObject();
    
    /**
     * Return a new singleton empty object
     */
    public static JsonObject emptyObject() {
        return new JsonObject();
    }
    
    /**
     * Create a new json object by cloning one object and then asserting all values from the second.
     * Shallow copy only.
     */
    public static JsonObject merge(JsonObject base, JsonObject extend) {
        JsonObject result = new JsonObject();
        for (Entry<String,JsonValue> entry : base.entrySet()) {
            result.put( entry.getKey(), entry.getValue() );
        }
        return mergeInto(result, extend);
    }
    
    /**
     * Merge all the extend values into the base object returning the base object, no copy.
     */
    public static JsonObject mergeInto(JsonObject base, JsonObject extend) {
        if (extend != null) {
            for (Entry<String,JsonValue> entry : extend.entrySet()) {
                base.put( entry.getKey(), entry.getValue() );
            }
        }
        return base;
    }
    
    /**
     * Parse a JSON or YAML file to an array of json objects, supports multi-document yaml files
     */
    public static JsonArray readArray(String filename, InputStream is) {
        if (filename.endsWith(".yaml") || filename.endsWith(".yml")) {
            JsonArray result = new JsonArray();
            for (Object doc : new Yaml().loadAll(is)) {
                result.add( asJson(doc) );
            }
            return result;
        } else {
            return JSON.parseAny(is).getAsArray();
        }
    }
    
    /**
     * Parse a JSON or YAML file to an array of json objects, supports multi-document yaml files
     * @throws FileNotFoundException 
     */
    public static JsonArray readArray(String filename) throws FileNotFoundException {
        InputStream is = new FileInputStream(filename);
        try {
            return readArray(filename, is);
        } finally {
            try {  is.close();  } catch (IOException e) { }
        }
    }
    
    /**
     * Parse a JSON or YAML file to a json object.
     */
    public static JsonObject readObject(String filename, InputStream is) {
        if (filename.endsWith(".yaml") || filename.endsWith(".yml")) {
            return asJson( new Yaml().load(is) ).getAsObject() ;
        } else {
            return JSON.parseAny(is).getAsObject();
        }
    }
    
    /**
     * Parse a JSON or YAML file to a json object.
     * @throws FileNotFoundException 
     */
    public static JsonObject readObject(String filename) throws FileNotFoundException {
        InputStream is = new FileInputStream(filename);
        try {
            return readObject(filename, is);
        } finally {
            try {  is.close();  } catch (IOException e) { }
        }
    }
}

