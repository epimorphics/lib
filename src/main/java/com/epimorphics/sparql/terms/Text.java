/******************************************************************
 * File:        Text.java
 * Created by:  Dave Reynolds
 * Created on:  24 Nov 2015
 * 
 * (c) Copyright 2015, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.sparql.terms;

import com.epimorphics.sparql.templates.Settings;

public class Text implements TripleOrFilter {
    protected String text;
    
    public Text(String text) {
        this.text = text;
    }

    @Override
    public void toSparql(Settings s, StringBuilder sb) {
        sb.append(text);
    }

}
