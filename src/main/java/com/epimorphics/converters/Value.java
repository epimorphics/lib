/******************************************************************
 * File:        Value.java
 * Created by:  Dave Reynolds
 * Created on:  5 Jul 2011
 * 
 * (c) Copyright 2011, Epimorphics Limited
 * $Id:  $
 *****************************************************************/

package com.epimorphics.converters;

/**
 * Wraps a value from a data source such as a CSV to simplify 
 * conversion to typed values. May be done by parsing a lexical form
 * or may be wrapped source which natively supports typed values (e.g. Excel).
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 * @version $Revision: $
 */
public interface Value {

    /** Original lexical form for the value */
    public String getLexical();

    /** Original lexical form for the value, Ruby-friendly alias */
    public String str();
    
    /** Return true if the value is non-null and not an empty string */
    public boolean notEmpty();
    
    /** Return true unless the value is null or an empty string */
    public boolean empty();
    
    /**
     * Return the value unconverted. For CVS source data this will be the
     * lexical form. For values injected by other sources may be any java
     * Object including boxed Integers etc
     */
    public Object getValue();

    /** Return value as a (long) int if possible, otherwise ModalException */
    public long asInt();
    
    /** Test if the value can be treated as a (long) int */
    public boolean isInt();
    

}

