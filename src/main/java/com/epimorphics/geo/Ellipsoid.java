/******************************************************************
 * File:        Ellipsoid.java
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
 * Specify an ellisoid used in geo coordinate reference systems
 * 
 * @author <a href="mailto:dave@epimorphics.com">Dave Reynolds</a>
 */
public class Ellipsoid {
    double a;  // major axis
    double b;  // minor axis
    double f;  // flattening
    
    public static Ellipsoid WGS84        = new Ellipsoid(6378137.0,   6356752.31425, 1/298.257223563);
    public static Ellipsoid GRS80        = new Ellipsoid(6378137.0,   6356752.31414, 1/298.257222101);
    public static Ellipsoid Airy1830     = new Ellipsoid(6377563.396, 6356256.909,   1/299.3249646);
    public static Ellipsoid AiryModified = new Ellipsoid(6377340.189, 6356034.448,   1/299.3249646);  
    public static Ellipsoid Intl1924     = new Ellipsoid(6378388.0,   6356911.946,   1/297);
    public static Ellipsoid Bessel1841   = new Ellipsoid(6377397.155, 6356078.963,   1/299.152815351);    
    
    public Ellipsoid(double a, double b, double f) {
        this.a = a;
        this.b = b;
        this.f = f;
    }
    
    public double getA() {
        return a;
    }
    
    public double getB() {
        return b;
    }

    public double getF() {
        return f;
    }
    
    public double getMajorAxis() {
        return a;
    }

    public double getMinorAxis() {
        return b;
    }

    public double getFlattening() {
        return f;
    }

}
