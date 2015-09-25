/******************************************************************
 * File:        GeoPoint.java
 * Created by:  Dave Reynolds
 * Created on:  1 Dec 2014
 * 
 * (c) Copyright 2014, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.geo;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * A geographic point identified by an OS grid reference lat lon.
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class GeoPoint {
    LatLonE latlon;
    OsGridRef gridref;
    
    public GeoPoint( LatLonE point ) {
        latlon = point;
    }
    
    public GeoPoint( OsGridRef gridref ) {
        this.gridref = gridref; 
    }
    
    /**
     * Construct point from lat lon pair in WGS84 CRS.
     */
    public static GeoPoint fromLatLon(double lat, double lon) {
        return new GeoPoint( new LatLonE(lat, lon) );
    }
    
    /**
     * Construct point from lat lon pair in OSGB36 CRS.
     */
    public static GeoPoint fromLatLonOSGB36(double lat, double lon) {
        return new GeoPoint( new LatLonE(lat, lon, LatLonDatum.OSGB36).convertDatum(LatLonDatum.WGS84) );
    }
    
    /**
     * Construct point from OS easting/northing pair
     */
    public static GeoPoint fromEastingNorthing(long easting, long northing) {
        return new GeoPoint( new OsGridRef(easting, northing) );
    }
    
    /**
     * Construct point from OS easting/northing pair
     */
    public static GeoPoint fromEastingNorthing(double easting, double northing) {
        return new GeoPoint( new OsGridRef(easting, northing) );
    }
    
    /**
     * Construct point from OS grid reference
     */
    public static GeoPoint fromGridRef(String gridref) {
        return new GeoPoint( OsGridRef.parse(gridref) );
    }
    
    /**
     * Return as lat lon WGS84 point
     */
    public LatLonE getLatLon()  {
        if (latlon == null) {
            latlon = gridref.toLatLon();
        }
        return latlon;
    }
    
    /**
     * Return as a grid reference
     */
    public OsGridRef getGridRef() {
        if (gridref == null) {
            gridref = OsGridRef.fromLatLon(latlon);
        }
        return gridref;
    }
    
    /**
     * Return WGS84 latitude
     */
    public double getLat() {
        return getLatLon().getLat();
    }
    
    /**
     * Return WGS84 longitude
     */
    public double getLon() {
        return getLatLon().getLon();
    }
    
    /**
     * Return WGS84 latitude as RDF literal 
     */
    public RDFNode getLatLiteral() {
        return ResourceFactory.createTypedLiteral( String.format("%f", getLat()), XSDDatatype.XSDdecimal);
    }
    
    /**
     * Return WGS84 longitude as RDF literal 
     */
    public RDFNode getLonLiteral() {
        return ResourceFactory.createTypedLiteral( String.format("%f", getLon()), XSDDatatype.XSDdecimal);
    }
    
    /**
     * Return OS easting
     */
    public long getEasting() {
        return getGridRef().getEasting();
    }
    
    /**
     * Return OS northing
     */
    public long getNorthing() {
        return getGridRef().getNorthing();
    }
    
    /**
     * Return OS grid reference string
     */
    public String getGridRefString(){
        return getGridRefString(10);
    }
    
    /**
     * Return OS grid reference with the given number of digit precision (10 = full precision, 5 digits for each numeric part)
     */
    public String getGridRefString(int digits){
        return getGridRef().format(digits);
    }

}
