/******************************************************************
 * File:        JsonWriter.java
 * Created by:  Dave Reynolds
 * Created on:  2 Aug 2013
 * 
 * (c) Copyright 2013, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.json;

import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;

/**
 * Variant on ARQ streaming JSON writer that supports full JSON numbers.
 * Can't subclass JSWriter it because the underlying writer is private.
 * Actually that's no longer true and this could now be migrated to a straight subclass.
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class JSFullWriter {
    
    private IndentedWriter out = IndentedWriter.stdout ;
    
    public JSFullWriter() { this(IndentedWriter.stdout) ; }
    public JSFullWriter(OutputStream ps) { this(new IndentedWriter(ps)) ; }
    public JSFullWriter(IndentedWriter ps) { out = ps ; }
    
    public void startOutput() {}
    public void finishOutput() { out.print("\n"); out.flush(); } 
    
    // These apply in nested and flat modes (the difference is controlled by the IndentedWriter
    
    private static String ArrayStart        = "[ " ;
    private static String ArrayFinish       = " ]" ;
    private static String ArraySep          = ", " ; 

    private static String ObjectStart       = "{ " ;
    private static String ObjectFinish      = "}" ;
    private static String ObjectSep         = " ," ;
    private static String ObjectPairSep     = " : " ;
    
    // Remember whether we are in the first element of a compound (object or array). 
    Deque<AtomicBoolean> stack = new ArrayDeque<>() ;
    
    public void startObject()
    {
        startCompound() ;
        out.print(ObjectStart) ;
        out.incIndent() ;
    }
    
    public void finishObject()
    {
        out.decIndent() ;
        if ( isFirst() )
            out.print(ObjectFinish) ;
        else
        {
            out.ensureStartOfLine() ;
            out.println(ObjectFinish) ;
        }
        finishCompound() ;
    }
    
    public void key(String key)
    {
        if ( isFirst() )
        {
            out.println();
            setNotFirst() ;
        }
        else
            out.println(ObjectSep) ;
        value(key) ;
        out.print(ObjectPairSep) ;
        // Ready to start the pair value.
    }
    
    // "Pair" is the name used in the JSON spec. 
    public void pair(String key, String value)
    {
        key(key) ;
        value(value) ;
    }
    
     
    public void pair(String key, boolean val)
    {
        key(key) ;
        value(val) ;
    }

    public void pair(String key, Number val)
    {
        key(key) ;
        value(val) ;
    }

    public void pair(String key, long val)
    {
        key(key) ;
        value(val) ;
    }
    
    public void pair(String key, JsonValue val) {
        key(key);
        value(val);
    }

    
    public void pair(String key, JSONWritable jw) {
        key(key);
        jw.writeTo(this);
    }

    protected void value(JsonValue val) {
        if (val.isBoolean()) {
            value( val.getAsBoolean().value() );
        } else if (val.isNumber()) {
            value( val.getAsNumber().value() );
        } else if (val.isString()) {
            value( val.getAsString().value() );
        } else if (val.isObject()) {
            value( val.getAsObject() );
        } else if (val.isArray()) {
            value( val.getAsArray() );
        } else {
            // skip nulls
        }
    }

    protected void value(JsonObject val) {
        startObject();
        for (Entry<String, JsonValue> entry : val.entrySet()) {
            pair( entry.getKey(), entry.getValue() );
        }
        finishObject();
    }
    
    protected void value(JsonArray val) {
        startArray();
        for (int i = 0; i < val.size(); i++) {
            arrayElementProcess();
            value( val.get(i) );
        }
        finishArray();
    }
    
    public void startArray()
    {
        startCompound() ;
        out.print(ArrayStart) ;
        // Messy with objects out.incIndent() ;
    }
     
    public void finishArray()
    {
//        out.decIndent() ;
        out.print(ArrayFinish) ;       // Leave on same line.
        finishCompound() ;
    }

    public void arrayElement(String str)
    {
        arrayElementProcess() ;
        value(str) ;
    }

    public void arrayElementProcess()
    {
        if ( isFirst() )
            setNotFirst() ;
        else
            out.print(ArraySep) ;
    }
    
    public void arrayElement(boolean b)
    {
        arrayElementProcess() ;
        value(b) ;
    }

    public void arrayElement(long integer)
    {
        arrayElementProcess() ;
        value(integer) ;
    }

    public void arrayElement(Number n)
    {
        arrayElementProcess() ;
        value(n) ;
    }
    
    /**
     * Useful if you are manually creating arrays and so need to print array separators yourself
     */
    public void arraySep()
    {
        out.print(ArraySep);
    }
    
    public static String outputQuotedString(String string)
    {
        // Use simple JSON string escaping since JSWriter method is no longer accessible
        return "\"" + string.replace("\\", "\\\\")
                           .replace("\"", "\\\"")
                           .replace("\n", "\\n")
                           .replace("\r", "\\r")
                           .replace("\t", "\\t") + "\"" ;
    }
    
    public void print(String x) {
        out.print(x);
    }
    

    private void startCompound() {
        stack.push(new AtomicBoolean(true)) ;
    }

    private void finishCompound() {
        stack.pop() ;
    }

    private boolean isFirst() {
        return stack.peek().get() ;
    }

    private void setNotFirst() {
        stack.peek().set(false) ;
    }

    
    // Can only write a value in some context.
    private void value(String x) { out.print(outputQuotedString(x)); }
    
    private void value(boolean b) { out.print(Boolean.toString(b)) ; }
    
    private void value(long integer) { out.print(Long.toString(integer)) ; }
    
    private void value(Number n) { out.print(n.toString()) ; }

}
