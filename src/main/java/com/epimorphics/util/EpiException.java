/******************************************************************
 * File:        EpiException.java
 * Created by:  Dave Reynolds
 * Created on:  22 Jan 2012
 * 
 * (c) Copyright 2012, Epimorphics Limited
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
