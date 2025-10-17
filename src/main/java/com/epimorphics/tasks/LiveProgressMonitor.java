/******************************************************************
 * File:        LiveProgressMonitor.java
 * Created by:  Dave Reynolds
 * Created on:  2 Dec 2013
 * 
 * (c) Copyright 2013, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.tasks;

/**
 * Simple progress monitor which reports progress messages live to stderr.
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class LiveProgressMonitor extends SimpleProgressMonitor {

    protected void reportStateChange() {
//        System.out.println("Progress state: " + state);
    }

    protected void reportNewMessage(ProgressMessage message) {
        super.reportNewMessage(message);
        System.err.println(message.toString());
    }
     
}
