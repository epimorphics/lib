/******************************************************************
 * File:        RawDataSource.java
 * Created by:  Dave Reynolds
 * Created on:  26 Jun 2011
 * 
 * (c) Copyright 2011, Epimorphics Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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
