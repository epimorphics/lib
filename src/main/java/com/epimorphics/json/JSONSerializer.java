/******************************************************************
 * File:        JSONSerializer.java
 * Created by:  Dave Reynolds
 * Created on:  2 May 2014
 * 
 * (c) Copyright 2014, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.json;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.epimorphics.util.EpiException;

/**
 * Support output of lists and maps to a JSON stream
 */
public class JSONSerializer implements JSONWritable {
    public Object root;
    
    public JSONSerializer(List<?> root) {
        this.root = root;
    }
    
    public JSONSerializer(Map<String, Object> root) {
        this.root = root;
    }
    
    @Override
    public void writeTo(JSFullWriter out) {
        write(root, out);
    }
    
    protected void write(Object x, JSFullWriter out) {
        if (x instanceof List<?>) {
            out.startArray();
            for (Iterator<?> i = ((List<?>)x).iterator(); i.hasNext();) {
                Object val = i.next();
                if (val instanceof String) {
                    out.arrayElement((String)val);
                } else if (val instanceof Boolean) {
                    out.arrayElement((Boolean)val);
                } else if (val instanceof Number) {
                    out.arrayElement((Number)val);
                } else {
                    write(val, out);
                    if (i.hasNext()) {
                        out.arraySep();
                    }
                }
            }
            out.finishArray();
        } else if (x instanceof Map<?, ?>) {
            out.startObject();
            for (Map.Entry<?,?> entry : ((Map<?,?>)x).entrySet()) {
                String key = entry.getKey().toString();
                Object val = entry.getValue();
                if (val instanceof String) {
                    out.pair(key, (String)val);
                } else if (val instanceof Boolean) {
                    out.pair(key, (Boolean)val);
                } else if (val instanceof Number) {
                    out.pair(key, (Number)val);
                } else {
                    out.key(key);
                    write(val,out);
                }
            }
            out.finishObject();
        } else {
            throw new EpiException("Illegal object type found while trying to serialize to json: " + x);
        }
    }

}
