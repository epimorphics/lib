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

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import com.epimorphics.util.NameUtils;

/**
 * Utilities for converting strings to typed RDF nodes
 * depending on either an explicit type URI or a guess from the syntax.
 */
public class TypeUtil {
    public static final Pattern INTEGER_PATTERN = Pattern.compile("(-\\s*)?[0-9]+");
    public static final Pattern DECIMAL_PATTERN = Pattern.compile("(-\\s*)?[0-9]+\\.[0-9]+");
    public static final Pattern FLOAT_PATTERN = Pattern.compile("(-\\s*)?[0-9]+(\\.[0-9]+)?[eE][-+]?[0-9]+(\\.[0-9]+)?");

    public static final String DATE_BLOCK = "[0-9]{4}-[01][0-9]-[0-3][0-9]";
    public static final String TIME_BLOCK = "[0-6][0-9]:[0-6][0-9]:[0-6][0-9](\\.[0-9]+)?";
    public static final String TZONE_BLOCK = "([+-][0-6][0-9]:[0-6][0-9])|Z";
    public static final String GYM_BLOCK = "[0-9]{4}-[01][0-9]";
    public static final Pattern DATETIME_PATTERN = Pattern.compile( String.format("-?%sT%s(%s)?", DATE_BLOCK, TIME_BLOCK, TZONE_BLOCK) );
    public static final Pattern DATE_PATTERN = Pattern.compile( String.format("-?%s(%s)?", DATE_BLOCK, TZONE_BLOCK) );
    public static final Pattern TIME_PATTERN = Pattern.compile( String.format("%s(%s)?", TIME_BLOCK, TZONE_BLOCK) );
    public static final Pattern GYEARMONTH_PATTERN = Pattern.compile( String.format("%s(%s)?", GYM_BLOCK, TZONE_BLOCK) );
    public static final Pattern ANYDATE_PATTERN = Pattern.compile( String.format("-?(%sT%s|%s|%s|%s)(%s)?", DATE_BLOCK, TIME_BLOCK, DATE_BLOCK, TIME_BLOCK, GYM_BLOCK, TZONE_BLOCK) );

    public static final Pattern URL_PATTERN = Pattern.compile("(http://|https://|ftp:|file:|mailto:).*");
    
    public static final String PLAIN_LITERAL_URI = RDF.getURI() + "PlainLiteral";    
    
    /**
     * Return a typed RDFNode based on a guess of the type from the syntax. 
     * Handles numbers, date/dateTimes and URIs. Defaults to a plain literal.
     */
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
        } else if (URL_PATTERN.matcher(value).matches()) {
            return ResourceFactory.createResource(value);
        } else {
            return ResourceFactory.createPlainLiteral(value);
        }
    }
    
    /**
     * Return a typed RDFNode using the given type URI.
     * If the type URI is null then the typed is guessed, if it is empty string then
     * a plain literal is used, if it is rdfs:Resource then a returns a resource.
     */
    public static RDFNode asTypedValue(String value, String typeURI, String lang) {
        if (typeURI == null) {
            return asTypedValue(value);
        } else if (typeURI.isEmpty() || PLAIN_LITERAL_URI.equals(typeURI)) {
            return ResourceFactory.createPlainLiteral(value);
        } else if (RDF.langString.getURI().equals(typeURI)) {
            if (value.contains("@")) {
                value = NameUtils.splitBeforeLast(value, "@");
                lang  = NameUtils.splitAfterLast(value, "@");
            }
            return ResourceFactory.createLangLiteral(value, lang);
        } else if (typeURI.equals(RDFS.Resource.getURI())) {
            return ResourceFactory.createResource(value);
        } else {
            RDFDatatype dt = TypeMapper.getInstance().getSafeTypeByName(typeURI);
            if (dt.isValid(value)) {
                return ResourceFactory.createTypedLiteral(value, dt);
            } else {
                throw new IllegalFormatException(value + " is not a legal syntax for type " + typeURI);
            }
        }
    }
    
    /**
     * Return a typed RDFNode using the given type URI.
     * If the type URI is null then the typed is guessed, if it is empty string then
     * a plain literal is used, if it is rdfs:Resource then a returns a resource.
     */
    public static RDFNode asTypedValue(String value, String typeURI) {
        return asTypedValue(value, typeURI, "en");
    }
    
    public static class IllegalFormatException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public IllegalFormatException(String message) {
            super(message);
        }
    }
}
