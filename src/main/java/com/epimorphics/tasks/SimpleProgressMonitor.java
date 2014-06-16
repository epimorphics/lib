/******************************************************************
 * File:        SimpleProgressMonitor.java
 * Created by:  Dave Reynolds
 * Created on:  11 Oct 2013
 * 
 * (c) Copyright 2013, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.tasks;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;

import static com.epimorphics.json.JsonUtil.*;

import com.epimorphics.json.JSFullWriter;
import com.epimorphics.json.JSONWritable;

/**
 * Simple implementation of progress monitor/reporter for in-process reporting.
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class SimpleProgressMonitor implements ProgressMonitorReporter, JSONWritable {    
    public static final String ID_FIELD = "id";
    public static final String PROGRESS_FIELD = "progress";
    public static final String STATE_FIELD    = "state";
    public static final String SUCEEDED_FIELD = "succeeded";
    public static final String MESSAGES_FIELD = "messages";

    protected String id;
    protected TaskState state = TaskState.Waiting;
    protected int progress = 0;
    protected boolean succeeded = true;
    protected List<ProgressMessage> messages = new ArrayList<>();
    protected long timestamp = System.currentTimeMillis();
    
    public SimpleProgressMonitor() {
        this.id = "anon";
    }
    
    public SimpleProgressMonitor(String id) {
        this.id = id;
    }
    
    public SimpleProgressMonitor(JsonObject stored) {
        this.id = getStringValue(stored, ID_FIELD);
        this.state = TaskState.valueOf( getStringValue(stored, STATE_FIELD) );
        this.succeeded = getBooleanValue(stored, SUCEEDED_FIELD, false);
        this.progress = getIntValue(stored, PROGRESS_FIELD, 0);
        Iterator<JsonValue> i = stored.get(MESSAGES_FIELD).getAsArray().iterator();
        while (i.hasNext()) {
            messages.add( new ProgressMessage( i.next().getAsObject() ) );
        }
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public synchronized void setState(TaskState state) {
        this.state = state;
        if (state == TaskState.Running) {
            progress = 1;
        } else if (state == TaskState.Waiting) {
            progress = 0;
        } else {
            progress = 100;
        }
        reportStateChange();
        notifyAll();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public synchronized void setProgress(int progress) {
        this.progress = progress;
        reportStateChange();
    }

    @Override
    public synchronized void setSuccess(boolean wasSuccessful) {
        succeeded = wasSuccessful;
//        reportStateChange();
        setState( TaskState.Terminated );
    }

    @Override
    public synchronized void setFailed() {
        succeeded = false;
        setState( TaskState.Terminated );
    }

    @Override
    public synchronized void report(String message) {
        reportNewMessage( new ProgressMessage(message) );
    }

    @Override
    public synchronized void report(String message, int lineNumber) {
        reportNewMessage( new ProgressMessage(message, lineNumber) );
    }
    
    protected void reportStateChange() {
    }

    protected void reportNewMessage(ProgressMessage message) {
        messages.add( message );
    }
    
    @Override
    public synchronized TaskState getState() {
        return state;
    }

    @Override
    public synchronized int getProgress() {
        return progress;
    }

    @Override
    public synchronized boolean succeeded() {
        return succeeded;
    }

    @Override
    public synchronized List<ProgressMessage> getMessages() {
        // Need to copy since will pass outside synchronization boundary
        return new ArrayList<ProgressMessage>(messages);
    }

    @Override
    public synchronized List<ProgressMessage> getMessagesSince(int offset) {
        // Need to copy since will pass outside synchronization boundary
        return new ArrayList<ProgressMessage>(messages.subList(offset, messages.size()));
    }

    @Override
    public synchronized boolean moreMessagesSince(int offset) {
        return messages.size() > offset;
    }
    
    @Override
    public String toString() {
        return String.format("Progress: %d %s(%s)", progress, state, succeeded ? "succeeded" : "failed");
    }
    
    /**
     * Return a truncated version of the monitor only containing message since a given offset point
     */
    public synchronized SimpleProgressMonitor truncate(int offset) {
        SimpleProgressMonitor clone = new SimpleProgressMonitor(id);
        clone.progress = progress;
        clone.state = state;
        clone.succeeded = succeeded;
        clone.timestamp = timestamp;
        
        clone.messages.addAll( getMessagesSince(offset) );
        return clone;
    }

    @Override
    public synchronized void writeTo(JSFullWriter out) {
        writeIncrement(out, 0);
    }
    
    protected synchronized void writeIncrement(JSFullWriter out, int offset) {
        out.startObject();
        out.pair(ID_FIELD, id);
        out.pair(STATE_FIELD, state.name());
        out.pair(PROGRESS_FIELD, progress);
        out.pair(SUCEEDED_FIELD, succeeded);
        out.key(MESSAGES_FIELD);
        out.startArray();
        int len = messages.size();
        for (int i = offset; i < len; i++) {
            messages.get(i).writeTo(out);
            if (i < len-1) {
                out.arraySep();
            }
        }
        out.finishArray();
        out.finishObject();
    }
    
    /**
     * Return a JSON view onto the status of the monitor including any messages
     * since the given offset.
     */
    public JSONWritable viewUpdatesSince(final int offset) {
        return new JSONWritable() {
            @Override
            public synchronized void writeTo(JSFullWriter out) {
                writeIncrement(out, offset);
            }
        };
    }

    @Override
    public void setSucceeded() {
        succeeded = true;
        setState(TaskState.Terminated);
    }

}
