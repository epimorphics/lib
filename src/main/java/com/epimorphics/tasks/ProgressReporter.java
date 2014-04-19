/******************************************************************
 * File:        ProgressReporter.java
 * Created by:  Dave Reynolds
 * Created on:  11 Oct 2013
 * 
 * (c) Copyright 2013, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.tasks;

/**
 * Interface through which an async task can report progress.
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public interface ProgressReporter {
    
    /**
     * Return an ID string for this monitor/reporter. 
     * Useful when managing a pool of persistent status reports.
     */
    public String getId();

    /**
     * Change the state of the progress report. 
     */
    public void setState(TaskState state);

    /**
     * Set the progress percentage.
     */
    public void setProgress(int progress);

    /**
     * Record whether the task was successful or not.
     */
    public void setSuccess(boolean wasSuccessful);
    
    /**
     * Record that a task has completed but successfully
     */
    public void setSucceeded();
    
    /**
     * Record that a task has completed but with an error.
     */
    public void failed();
    
    /**
     * Log a progress message, this may be timestamped by the reporter implementation.
     */
    public void report(String message);
    
    /**
     * Log a progress message with an associated line number, this may be timestamped by the reporter implementation.
     * 
     */
    public void report(String message, int lineNumber);
    

    /**
     * Return the state of the task. It may be Waiting to start,
     * Running or Terminated.
     */
    public TaskState getState();
    
}
