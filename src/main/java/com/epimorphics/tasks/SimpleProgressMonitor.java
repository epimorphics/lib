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

import com.epimorphics.json.JSFullWriter;
import com.epimorphics.json.JSONWritable;

/**
 * Simple implementation of progress monitor/reporter for in-process reporting.
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class SimpleProgressMonitor implements ProgressMonitor, ProgressReporter, JSONWritable {    
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
        reportStateChange();
    }

    @Override
    public synchronized void failed() {
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
        return messages;
    }

    @Override
    public synchronized List<ProgressMessage> getMessagesSince(int offset) {
        return messages.subList(offset, messages.size());
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
    public void writeTo(JSFullWriter out) {
        out.startObject();
        out.pair(STATE_FIELD, state.name());
        out.pair(PROGRESS_FIELD, progress);
        out.pair(SUCEEDED_FIELD, succeeded);
        out.key(MESSAGES_FIELD);
        out.startArray();
        for (Iterator<ProgressMessage> mi = messages.iterator(); mi.hasNext();) {
            mi.next().writeTo(out);
            if (mi.hasNext()) {
                out.arraySep();
            }
        }
        out.finishArray();
        out.finishObject();
    }

    @Override
    public void setSucceeded() {
        succeeded = true;
        setState(TaskState.Terminated);
    }

}
