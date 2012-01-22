/******************************************************************
 * File:        EpiException.java
 * Created by:  Dave Reynolds
 * Created on:  22 Jan 2012
 * 
 * (c) Copyright 2012, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.util;

/**
 * Generic runtime exception wrapper for those times when you want
 * a generic unchecked exception but don't mean it to be specific
 * to any particular subsystem such a Jena.
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
@SuppressWarnings("serial")
public class EpiException extends RuntimeException 
{
    public EpiException() {
        super();
    }
    
    public EpiException(String message) {
        super(message); 
    }
    
    public EpiException(Throwable cause) { 
        super(cause) ;
    }
    
    public EpiException(String message, Throwable cause) { 
        super(message, cause) ;
    }

}
