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

import com.epimorphics.util.EpiException;


/**
 * Wraps a value from a data source such as a CSV to simply 
 * conversion to typed values. Failed conversions throw generic ModalException (Change this?).
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 * @version $Revision: $
 */
public class SimpleValue implements Value {

    String lex;
    Object value;
    
    public SimpleValue(String lexicalForm) {
        lex = lexicalForm;
        value = lex;
    }
    
    public SimpleValue(Object value) {
        this.value = value;
    }
        
    @Override
    public String getLexical() {
        if (lex == null) {
            return value.toString();
        } else {
            return lex;
        }
    }
    
    @Override
    public long asInt() {
        if (value != null && value instanceof Number) {
            return ((Number)value).longValue();
        }
        try {
            return Long.parseLong(lex);
        } catch (NumberFormatException e) {
            throw new EpiException("Value conversion error on " + lex, e);
        }
    }
    
    @Override
    public boolean isInt() {
        if (value != null && value instanceof Number) {
            return true;
        }
        try {
            Long.parseLong(lex);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return getLexical();
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public boolean notEmpty() {
        if (lex != null) {
            return !lex.isEmpty();
        } else {
            return value != null;
        }
    }

    @Override
    public boolean empty() {
        return !notEmpty();
    }

    @Override
    public String str() {
        return getLexical();
    }
    
}

