/******************************************************************
 * File:        Value.java
 * Created by:  Dave Reynolds
 * Created on:  5 Jul 2011
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

