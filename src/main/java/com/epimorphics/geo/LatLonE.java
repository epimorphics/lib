/******************************************************************
 * File:        LatLonE.java
 * Created by:  Dave Reynolds
 * Created on:  1 Dec 2014
 * 
 * Based on:
 * https://github.com/chrisveness/geodesy
 * (c) Chris Veness 2011-2014 / MIT Licence
 * 
 * (c) Copyright 2014, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.geo;

/**
 * lat/lon (polar) point with latitude & longitude values and height above ellipsoid, on a
 * specified datum.
 */
public class LatLonE {
    double lat;
    double lon;
    LatLonDatum datum;
    double height;
    
    public LatLonE(double lat, double lon, LatLonDatum datum, double height) {
        this.lat = lat;
        this.lon = lon;
        this.datum = datum;
        this.height = height;
    }
    
    public LatLonE(double lat, double lon, LatLonDatum datum) {
        this(lat, lon, datum, 0.0);
    }
    
    public LatLonE(double lat, double lon, double height) {
        this(lat, lon, LatLonDatum.WGS84, height);
    }
    
    public LatLonE(double lat, double lon) {
        this(lat, lon, LatLonDatum.WGS84, 0.0);
    }

    
    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    /**
     * Converts ‘this’ lat/lon coordinate to new coordinate system.
     *
     * @param   {LatLonE.datum} toDatum - Datum this coordinate is to be converted to.
     * @returns {LatLonE} This point converted to new datum.
     *
     * @example
     *     var pWGS84 = new LatLonE(51.4778, -0.0016, LatLonE.datum.WGS84);
     *     var pOSGB = pWGS84.convertDatum(LatLonE.datum.OSGB36); // pOSGB.toString(): 51.4773°N, 000.0000°E
     */
    public LatLonE convertDatum(LatLonDatum toDatum) {
        LatLonE oldLatLon = this;
        HelmertTransform transform = null;
    
        if (oldLatLon.datum == LatLonDatum.WGS84) {
            // converting from WGS 84
            transform = toDatum.transform;
        }
        if (toDatum == LatLonDatum.WGS84) {
            transform = oldLatLon.datum.transform.inverse();
        }
        if (transform == null) {
            // neither this.datum nor toDatum are WGS84: convert this to WGS84 first
            oldLatLon = this.convertDatum(LatLonDatum.WGS84);
            transform = toDatum.transform;
        }
    
        // convert polar to cartesian
        Vector3d cartesian = oldLatLon.toCartesian();
    
        // apply transform
        cartesian = cartesian.applyTransform(transform);
    
        // convert cartesian to polar
        LatLonE newLatLon = cartesian.toLatLon(toDatum);
    
        return newLatLon;
    };


    /**
     * Converts ‘this’ point from polar (lat/lon) coordinates to cartesian (x/y/z) coordinates.
     *
     * @returns {Vector3d} Vector pointing to lat/lon point, with x, y, z in metres from earth centre.
     */
    public Vector3d toCartesian() {
        double φ = Math.toRadians(lat), λ = Math.toRadians(lon), H = this.height;
        double a = this.datum.ellipsoid.a, b = this.datum.ellipsoid.b;
    
        double sinφ = Math.sin(φ), cosφ = Math.cos(φ);
        double sinλ = Math.sin(λ), cosλ = Math.cos(λ);
    
        double eSq = (a*a - b*b) / (a*a);
        double ν = a / Math.sqrt(1 - eSq*sinφ*sinφ);
    
        double x = (ν+H) * cosφ * cosλ;
        double y = (ν+H) * cosφ * sinλ;
        double z = ((1-eSq)*ν + H) * sinφ;
    
        return new Vector3d(x, y, z);
    };

}
