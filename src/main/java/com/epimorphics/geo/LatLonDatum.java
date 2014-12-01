/******************************************************************
 * File:        LatLonDatum.java
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
 * Datums; with associated *ellipsoid* and Helmert *transform* parameters to convert from WGS 84
 * into given datum.
 */
public class LatLonDatum {
    protected Ellipsoid ellipsoid;
    protected HelmertTransform transform;
    
    public static LatLonDatum WGS84 = new LatLonDatum(
            Ellipsoid.WGS84, 
            new HelmertTransform(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0) );
    
    // (2009); functionally ≡ WGS84 - www.uvm.edu/giv/resources/WGS84_NAD83.pdf
    // note: if you *really* need to convert WGS84<->NAD83, you need more knowledge than this!
    public static LatLonDatum NAD83 = new LatLonDatum(
            Ellipsoid.GRS80, 
            new HelmertTransform(1.004, -1.910, -0.515, 0.0267, 0.00034, 0.011, -0.0015) );
    
    public static LatLonDatum OSGB36 = new LatLonDatum(
            Ellipsoid.Airy1830, 
            new HelmertTransform(-446.448, 125.157, -542.060, -0.1502, -0.2470, -0.8421, 20.4894) );
    
    public static LatLonDatum ED50 = new LatLonDatum(
            Ellipsoid.Intl1924, 
            new HelmertTransform(89.5, 93.8, 123.1, 0.0, 0.0, 0.156, -1.2 ) );
    
    public static LatLonDatum Irl1975 = new LatLonDatum(
            Ellipsoid.AiryModified,
            new HelmertTransform(-482.530, 130.596, -564.557, -1.042, -0.214, -0.631, -8.150) );
    
    public static LatLonDatum TokyoJapan = new LatLonDatum(
            Ellipsoid.Bessel1841, 
            new HelmertTransform(148, -507, -685, 0, 0, 0, 0) );
    
    public LatLonDatum(Ellipsoid ellipsoid, HelmertTransform transform) {
        this.ellipsoid = ellipsoid;
        this.transform = transform;
    }

}
