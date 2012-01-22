package com.epimorphics.vocabs;
/* CVS $Id: $ */
 
import com.hp.hpl.jena.rdf.model.*;
 
/**
 * Vocabulary definitions from src/main/webapp/vocabs/time.ttl 
 * @author Auto-generated by schemagen on 16 Nov 2010 11:39 
 */
public class Time {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.w3.org/2006/time#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    public static final Property after = m_model.createProperty( "http://www.w3.org/2006/time#after" );
    
    public static final Property before = m_model.createProperty( "http://www.w3.org/2006/time#before" );
    
    public static final Property day = m_model.createProperty( "http://www.w3.org/2006/time#day" );
    
    public static final Property dayOfWeek = m_model.createProperty( "http://www.w3.org/2006/time#dayOfWeek" );
    
    public static final Property dayOfYear = m_model.createProperty( "http://www.w3.org/2006/time#dayOfYear" );
    
    public static final Property days = m_model.createProperty( "http://www.w3.org/2006/time#days" );
    
    public static final Property hasBeginning = m_model.createProperty( "http://www.w3.org/2006/time#hasBeginning" );
    
    public static final Property hasDateTimeDescription = m_model.createProperty( "http://www.w3.org/2006/time#hasDateTimeDescription" );
    
    public static final Property hasDurationDescription = m_model.createProperty( "http://www.w3.org/2006/time#hasDurationDescription" );
    
    public static final Property hasEnd = m_model.createProperty( "http://www.w3.org/2006/time#hasEnd" );
    
    public static final Property hour = m_model.createProperty( "http://www.w3.org/2006/time#hour" );
    
    public static final Property hours = m_model.createProperty( "http://www.w3.org/2006/time#hours" );
    
    public static final Property inDateTime = m_model.createProperty( "http://www.w3.org/2006/time#inDateTime" );
    
    public static final Property inXSDDateTime = m_model.createProperty( "http://www.w3.org/2006/time#inXSDDateTime" );
    
    public static final Property inside = m_model.createProperty( "http://www.w3.org/2006/time#inside" );
    
    public static final Property intervalAfter = m_model.createProperty( "http://www.w3.org/2006/time#intervalAfter" );
    
    public static final Property intervalBefore = m_model.createProperty( "http://www.w3.org/2006/time#intervalBefore" );
    
    public static final Property intervalContains = m_model.createProperty( "http://www.w3.org/2006/time#intervalContains" );
    
    public static final Property intervalDuring = m_model.createProperty( "http://www.w3.org/2006/time#intervalDuring" );
    
    public static final Property intervalEquals = m_model.createProperty( "http://www.w3.org/2006/time#intervalEquals" );
    
    public static final Property intervalFinishedBy = m_model.createProperty( "http://www.w3.org/2006/time#intervalFinishedBy" );
    
    public static final Property intervalFinishes = m_model.createProperty( "http://www.w3.org/2006/time#intervalFinishes" );
    
    public static final Property intervalMeets = m_model.createProperty( "http://www.w3.org/2006/time#intervalMeets" );
    
    public static final Property intervalMetBy = m_model.createProperty( "http://www.w3.org/2006/time#intervalMetBy" );
    
    public static final Property intervalOverlappedBy = m_model.createProperty( "http://www.w3.org/2006/time#intervalOverlappedBy" );
    
    public static final Property intervalOverlaps = m_model.createProperty( "http://www.w3.org/2006/time#intervalOverlaps" );
    
    public static final Property intervalStartedBy = m_model.createProperty( "http://www.w3.org/2006/time#intervalStartedBy" );
    
    public static final Property intervalStarts = m_model.createProperty( "http://www.w3.org/2006/time#intervalStarts" );
    
    public static final Property minute = m_model.createProperty( "http://www.w3.org/2006/time#minute" );
    
    public static final Property minutes = m_model.createProperty( "http://www.w3.org/2006/time#minutes" );
    
    public static final Property month = m_model.createProperty( "http://www.w3.org/2006/time#month" );
    
    public static final Property months = m_model.createProperty( "http://www.w3.org/2006/time#months" );
    
    public static final Property second = m_model.createProperty( "http://www.w3.org/2006/time#second" );
    
    public static final Property seconds = m_model.createProperty( "http://www.w3.org/2006/time#seconds" );
    
    public static final Property timeZone = m_model.createProperty( "http://www.w3.org/2006/time#timeZone" );
    
    public static final Property unitType = m_model.createProperty( "http://www.w3.org/2006/time#unitType" );
    
    public static final Property week = m_model.createProperty( "http://www.w3.org/2006/time#week" );
    
    public static final Property weeks = m_model.createProperty( "http://www.w3.org/2006/time#weeks" );
    
    public static final Property xsdDateTime = m_model.createProperty( "http://www.w3.org/2006/time#xsdDateTime" );
    
    public static final Property year = m_model.createProperty( "http://www.w3.org/2006/time#year" );
    
    public static final Property years = m_model.createProperty( "http://www.w3.org/2006/time#years" );
    
    public static final Resource DateTimeDescription = m_model.createResource( "http://www.w3.org/2006/time#DateTimeDescription" );
    
    public static final Resource DateTimeInterval = m_model.createResource( "http://www.w3.org/2006/time#DateTimeInterval" );
    
    public static final Resource DayOfWeek = m_model.createResource( "http://www.w3.org/2006/time#DayOfWeek" );
    
    public static final Resource DurationDescription = m_model.createResource( "http://www.w3.org/2006/time#DurationDescription" );
    
    public static final Resource Instant = m_model.createResource( "http://www.w3.org/2006/time#Instant" );
    
    public static final Resource Interval = m_model.createResource( "http://www.w3.org/2006/time#Interval" );
    
    public static final Resource January = m_model.createResource( "http://www.w3.org/2006/time#January" );
    
    public static final Resource ProperInterval = m_model.createResource( "http://www.w3.org/2006/time#ProperInterval" );
    
    public static final Resource TemporalEntity = m_model.createResource( "http://www.w3.org/2006/time#TemporalEntity" );
    
    public static final Resource TemporalUnit = m_model.createResource( "http://www.w3.org/2006/time#TemporalUnit" );
    
    public static final Resource Year = m_model.createResource( "http://www.w3.org/2006/time#Year" );
    
    public static final Resource Friday = m_model.createResource( "http://www.w3.org/2006/time#Friday" );
    
    public static final Resource Monday = m_model.createResource( "http://www.w3.org/2006/time#Monday" );
    
    public static final Resource Saturday = m_model.createResource( "http://www.w3.org/2006/time#Saturday" );
    
    public static final Resource Sunday = m_model.createResource( "http://www.w3.org/2006/time#Sunday" );
    
    public static final Resource Thursday = m_model.createResource( "http://www.w3.org/2006/time#Thursday" );
    
    public static final Resource Tuesday = m_model.createResource( "http://www.w3.org/2006/time#Tuesday" );
    
    public static final Resource Wednesday = m_model.createResource( "http://www.w3.org/2006/time#Wednesday" );
    
    public static final Resource unitDay = m_model.createResource( "http://www.w3.org/2006/time#unitDay" );
    
    public static final Resource unitHour = m_model.createResource( "http://www.w3.org/2006/time#unitHour" );
    
    public static final Resource unitMinute = m_model.createResource( "http://www.w3.org/2006/time#unitMinute" );
    
    public static final Resource unitMonth = m_model.createResource( "http://www.w3.org/2006/time#unitMonth" );
    
    public static final Resource unitSecond = m_model.createResource( "http://www.w3.org/2006/time#unitSecond" );
    
    public static final Resource unitWeek = m_model.createResource( "http://www.w3.org/2006/time#unitWeek" );
    
    public static final Resource unitYear = m_model.createResource( "http://www.w3.org/2006/time#unitYear" );
    
}
