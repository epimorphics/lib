/******************************************************************
 * File:        ProgressReort.java
 * Created by:  Dave Reynolds
 * Created on:  11 Oct 2013
 * 
 * (c) Copyright 2013, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.tasks;

import static com.epimorphics.json.JsonUtil.getIntValue;
import static com.epimorphics.json.JsonUtil.getLongValue;
import static com.epimorphics.json.JsonUtil.getStringValue;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jena.atlas.json.JsonObject;

import com.epimorphics.json.JSFullWriter;
import com.epimorphics.json.JSONWritable;

/**
 * Simple progress message format which can be serialized to JSON.
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class ProgressMessage implements JSONWritable {
    public static final String MESSAGE_FIELD     = "message";
    public static final String RAW_MESSAGE_FIELD     = "raw_message";
    public static final String LINE_NUMBER_FIELD = "lineNumber";
    public static final String TIMESTAMP_FIELD   = "timestamp";
    public static final String TYPE_FIELD       = "type";
    
    protected static final int NULL_LINE_NUMBER = -1;
    
    String message;
    long timestamp;
    int lineNumber;
    String type = "";
    
    public ProgressMessage(String message) {
        this(message, NULL_LINE_NUMBER);
    }
    
    public ProgressMessage(String message, int lineNumber) {
        this(message, lineNumber, System.currentTimeMillis());
    }
    
    public ProgressMessage(String message, int lineNumber, long timestamp) {
        this.message = message;
        this.lineNumber = lineNumber;
        this.timestamp = timestamp;
    }
    
    public ProgressMessage(String message, String style) {
        this(message, NULL_LINE_NUMBER, style);
    }
    
    public ProgressMessage(String message, int lineNumber, String style) {
        this(message, lineNumber, System.currentTimeMillis(), style);
    }
    
    public ProgressMessage(String message, int lineNumber, long timestamp, String style) {
        this.message = message;
        this.lineNumber = lineNumber;
        this.timestamp = timestamp;
        this.type = style;
    }
    
    public ProgressMessage(JsonObject json) {
        this(   getStringValue(json, RAW_MESSAGE_FIELD, ""),
                getIntValue(json, LINE_NUMBER_FIELD, NULL_LINE_NUMBER),
                getLongValue(json, TIMESTAMP_FIELD, System.currentTimeMillis()),
                getStringValue(json, TYPE_FIELD, "")
                );
    }
    
    
    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
    
    public String getTimelabel() {
        return  String.format("%tT.%tL", timestamp, timestamp);
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return String.format("%tT.%tL %s", timestamp, timestamp, cleanMessage()) + (lineNumber == NULL_LINE_NUMBER ? "" : " [" + lineNumber + "]");
    }
    
    protected String cleanMessage() {
        String clean = StringEscapeUtils.escapeHtml(message);
        return CHEF_JUNK.matcher(clean).replaceAll("");
    }
    static final Pattern CHEF_JUNK = Pattern.compile("\u001B\\[\\d*m");
    
    @Override
    public void writeTo(JSFullWriter out) {
        out.startObject();
        out.pair(TIMESTAMP_FIELD, timestamp);
        out.pair(RAW_MESSAGE_FIELD, message);
        out.pair(MESSAGE_FIELD, toString());
        out.pair(TYPE_FIELD, type);
        if (lineNumber != NULL_LINE_NUMBER) {
            out.pair(LINE_NUMBER_FIELD, lineNumber);
        }
        out.finishObject();
    }
    
}
