/******************************************************************
 * File:        TypeUtil.java
 * Created by:  Dave Reynolds
 * Created on:  11 Jan 2015
 * 
 * (c) Copyright 2015, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.rdfutil;

import java.util.regex.Pattern;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * Utilities for converting strings to typed RDF nodes
 * depending on either an explicit type URI or a guess from the syntax.
 */
public class TypeUtil {
    protected static final Pattern INTEGER_PATTERN = Pattern.compile("(-\\s*)?[0-9]+");
    protected static final Pattern DECIMAL_PATTERN = Pattern.compile("(-\\s*)?[0-9]+\\.[0-9]+");
    protected static final Pattern FLOAT_PATTERN = Pattern.compile("(-\\s*)?[0-9]+(\\.[0-9]+)?[eE][-+]?[0-9]+(\\.[0-9]+)?");

    protected static final String DATE_BLOCK = "[0-9]{4}-[01][0-9]-[0-3][0-9]";
    protected static final String TIME_BLOCK = "[0-6][0-9]:[0-6][0-9]:[0-6][0-9](\\.[0-9]+)?";
    protected static final String TZONE_BLOCK = "([+-][0-6][0-9]:[0-6][0-9])|Z";
    protected static final String GYM_BLOCK = "[0-9]{4}-[01][0-9]";
    protected static final Pattern DATETIME_PATTERN = Pattern.compile( String.format("-?%sT%s(%s)?", DATE_BLOCK, TIME_BLOCK, TZONE_BLOCK) );
    protected static final Pattern DATE_PATTERN = Pattern.compile( String.format("-?%s(%s)?", DATE_BLOCK, TZONE_BLOCK) );
    protected static final Pattern TIME_PATTERN = Pattern.compile( String.format("%s(%s)?", TIME_BLOCK, TZONE_BLOCK) );
    protected static final Pattern GYEARMONTH_PATTERN = Pattern.compile( String.format("%s(%s)?", GYM_BLOCK, TZONE_BLOCK) );
    protected static final Pattern ANYDATE_PATTERN = Pattern.compile( String.format("-?(%sT%s|%s|%s|%s)(%s)?", DATE_BLOCK, TIME_BLOCK, DATE_BLOCK, TIME_BLOCK, GYM_BLOCK, TZONE_BLOCK) );

    public static RDFNode asTypedValue(String value){
        if (INTEGER_PATTERN.matcher(value).matches()) {
            return ResourceFactory.createTypedLiteral(value, XSDDatatype.XSDinteger);
        } else if (DECIMAL_PATTERN.matcher(value).matches()) {
            return ResourceFactory.createTypedLiteral(value, XSDDatatype.XSDdecimal);
        } else if (FLOAT_PATTERN.matcher(value).matches()) {
            return ResourceFactory.createTypedLiteral(value, XSDDatatype.XSDdouble);
        } else if (DATETIME_PATTERN.matcher(value).matches()) {
            return ResourceFactory.createTypedLiteral(value, XSDDatatype.XSDdateTime);
        } else if (DATE_PATTERN.matcher(value).matches()) {
            return ResourceFactory.createTypedLiteral(value, XSDDatatype.XSDdate);
        } else if (GYEARMONTH_PATTERN.matcher(value).matches()) {
            return ResourceFactory.createTypedLiteral(value, XSDDatatype.XSDgYearMonth);
        } else {
            return ResourceFactory.createPlainLiteral(value);
        }
    }
    
    public static RDFNode asTypedValue(String value, String typeURI) {
        if (typeURI == null) {
            return ResourceFactory.createPlainLiteral(value);
        } else {
            RDFDatatype dt = TypeMapper.getInstance().getSafeTypeByName(typeURI);
            if (dt.isValid(value)) {
                return ResourceFactory.createTypedLiteral(value, dt);
            } else {
                throw new IllegalFormatException(value + " is not a legal syntax for type " + typeURI);
            }
        }
    }
    
    public static class IllegalFormatException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public IllegalFormatException(String message) {
            super(message);
        }
    }
}
