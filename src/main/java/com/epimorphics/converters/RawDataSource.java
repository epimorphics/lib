/******************************************************************
 * File:        RawDataSource.java
 * Created by:  Dave Reynolds
 * Created on:  26 Jun 2011
 * 
 * (c) Copyright 2011, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.converters;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Data stream to be converted by some DataConverter.
 * Current abstraction is of a series of records each of which has a set of named fields.
 * The field values are unconstrained and in particular may include structured values reported
 * as Maps. Simple field values will be reported boxed as Value instances.
 * 
 * Experimental - only suited to tabluar structure data.
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public interface RawDataSource extends Iterable<Map<String,Object>>, Iterator<Map<String,Object>>{

    /** variable name under which the row of data will be bound in the environment */
    public static final String ROW = "_row";

    /** variable name under which the index of the row will be bound in the environment */
    public static final String INDEX = "_index";

    /** 
     * Return the set of top level field names in each record
     */
    public List<String> getFieldNames();
    
    /**
     * Return true if more record values are available.
     */
    public boolean hasNext();

    /**
     * Return the next record value.
     */
    public Map<String, Object> next();

    /**
     * Return itself as an iterator
     */
    public Iterator<Map<String, Object>> iterator();
    
    /** 
     * Return the total number of rows in the source or -1 if this information is not available 
     * */
    public long size();
    
}
