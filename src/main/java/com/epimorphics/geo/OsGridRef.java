/******************************************************************
 * File:        OsGridRef.java
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

import com.epimorphics.util.EpiException;

public class OsGridRef {
    long easting;
    long northing;
    
    public OsGridRef(long easting, long northing) {
        this.easting = easting;
        this.northing = northing;
    }
    
    public OsGridRef(double easting, double northing) {
        this.easting = (long) Math.floor(easting);
        this.northing = (long) Math.floor(northing);
    }
    
    
    public long getEasting() {
        return easting;
    }

    public long getNorthing() {
        return northing;
    }

    /**
     * Parse grid reference to easting/northing form
     */
    public static OsGridRef parse(String gridref) {
        gridref = gridref.trim().replaceAll(" ", "");
        
        // get numeric values of letter references, mapping A->0, B->1, C->2, etc:
        int l1 = Character.toUpperCase( gridref.charAt(0)) - 'A';
        int l2 = Character.toUpperCase( gridref.charAt(1)) - 'A';
        // shuffle down letters after 'I' since 'I' is not used in grid:
        if (l1 > 7) l1--;
        if (l2 > 7) l2--;

        // convert grid letters into 100km-square indexes from false origin (grid square SV):
        long e = ((l1-2)%5)*5 + (l2%5);
        long n = (long) ( (19-Math.floor(l1/5)*5) - Math.floor(l2/5) );
        if (e<0 || e>6 || n<0 || n>12) return null;

        gridref = gridref.substring(2);

        // append numeric part of references to grid index:
        int len = gridref.length() / 2;
        String eʹ = "" + e +  gridref.substring(0, len);
        String nʹ = "" + n +  gridref.substring(len); 

        switch(len) {
        case 0: eʹ += "50000"; nʹ += "50000"; break; 
        case 1: eʹ += "5000";  nʹ += "5000"; break; 
        case 2: eʹ += "500";   nʹ += "500"; break; 
        case 3: eʹ += "50";    nʹ += "50"; break; 
        case 4: eʹ += "5";     nʹ += "5"; break;
        case 5: break;
        }
        
        return new OsGridRef(Long.parseLong(eʹ), Long.parseLong(nʹ));
    }
    
    /**
     * Return as a grid reference string to given total digit precision (i.e. 10 = 5 digit easting/northing = 1m resolution)
     */
    public String format(int digits) {
        long e = this.easting;
        long n = this.northing;

        // get the 100km-grid indices
        long e100k = (long) Math.floor(e/100000), n100k = (long) Math.floor(n/100000);

        if (e100k<0 || e100k>6 || n100k<0 || n100k>12) return "";

        // translate those into numeric equivalents of the grid letters
        int l1 = (int)( (19-n100k) - (19-n100k)%5 + Math.floor((e100k+10)/5) );
        int l2 = (int)( (19-n100k)*5%25 + e100k%5 );

        // compensate for skipped 'I' and build grid letter-pairs
        if (l1 > 7) l1++;
        if (l2 > 7) l2++;
        char char1 = (char) ('A' + l1);
        char char2 = (char) ('A' + l2);

        // strip 100km-grid indices from easting & northing, and reduce precision
        e = (long) Math.floor((e%100000)/Math.pow(10,5-digits/2));
        n = (long) Math.floor((n%100000)/Math.pow(10,5-digits/2));

        String formatString = "%" + (int)(digits/2) + "d";
        formatString = "%c%c " + formatString + " " + formatString;
        String gridRef = String.format(formatString, char1, char2, e, n);

        return gridRef;        
    }
    
    /**
     * Create grid reference from OSGB36 or WGS84 lat, lon
     */
    public static OsGridRef fromLatLon(LatLonE point) {
        if (point.datum == LatLonDatum.OSGB36) {
            return fromLatLonOSGB36(point);
        } else {
            return fromLatLonOSGB36( point.convertDatum(LatLonDatum.OSGB36) );
        }
    }
    
    /**
     * Create grid reference from OSGB36 lat, lon
     */
    public static OsGridRef fromLatLonOSGB36(LatLonE point) {
        if (point.datum != LatLonDatum.OSGB36) throw new EpiException("Can only convert OSGB36 point to OsGrid");
        double φ = Math.toRadians( point.lat );
        double λ = Math.toRadians( point.lon );

        double a = 6377563.396, b = 6356256.909;              // Airy 1830 major & minor semi-axes
        double F0 = 0.9996012717;                             // NatGrid scale factor on central meridian
        double φ0 = Math.toRadians(49), λ0 = Math.toRadians(-2);  // NatGrid true origin is 49°N,2°W
        double N0 = -100000, E0 = 400000;                     // northing & easting of true origin, metres
        double e2 = 1 - (b*b)/(a*a);                          // eccentricity squared
        double n = (a-b)/(a+b), n2 = n*n, n3 = n*n*n;         // n, n², n³

        double cosφ = Math.cos(φ), sinφ = Math.sin(φ);
        double ν = a*F0/Math.sqrt(1-e2*sinφ*sinφ);            // nu = transverse radius of curvature
        double ρ = a*F0*(1-e2)/Math.pow(1-e2*sinφ*sinφ, 1.5); // rho = meridional radius of curvature
        double η2 = ν/ρ-1;                                    // eta = ?

        double Ma = (1 + n + (5/4)*n2 + (5/4)*n3) * (φ-φ0);
        double Mb = (3*n + 3*n*n + (21/8)*n3) * Math.sin(φ-φ0) * Math.cos(φ+φ0);
        double Mc = ((15/8)*n2 + (15/8)*n3) * Math.sin(2*(φ-φ0)) * Math.cos(2*(φ+φ0));
        double Md = (35/24)*n3 * Math.sin(3*(φ-φ0)) * Math.cos(3*(φ+φ0));
        double M = b * F0 * (Ma - Mb + Mc - Md);              // meridional arc

        double cos3φ = cosφ*cosφ*cosφ;
        double cos5φ = cos3φ*cosφ*cosφ;
        double tan2φ = Math.tan(φ)*Math.tan(φ);
        double tan4φ = tan2φ*tan2φ;

        double I = M + N0;
        double II = (ν/2)*sinφ*cosφ;
        double III = (ν/24)*sinφ*cos3φ*(5-tan2φ+9*η2);
        double IIIA = (ν/720)*sinφ*cos5φ*(61-58*tan2φ+tan4φ);
        double IV = ν*cosφ;
        double V = (ν/6)*cos3φ*(ν/ρ-tan2φ);
        double VI = (ν/120) * cos5φ * (5 - 18*tan2φ + tan4φ + 14*η2 - 58*tan2φ*η2);

        double Δλ = λ-λ0;
        double Δλ2 = Δλ*Δλ, Δλ3 = Δλ2*Δλ, Δλ4 = Δλ3*Δλ, Δλ5 = Δλ4*Δλ, Δλ6 = Δλ5*Δλ;

        double N = I + II*Δλ2 + III*Δλ4 + IIIA*Δλ6;
        double E = E0 + IV*Δλ + V*Δλ3 + VI*Δλ5;

        return new OsGridRef(E, N); // gets truncated to SW corner of 1m grid square        
    }
    
    /**
     * Return as a lat lon point in OSGB36
     */
    public LatLonE toLotLonOSGB36() {
        double E = easting + 0.5;  // easting of centre of 1m grid square
        double N = northing + 0.5; // northing of centre of 1m grid square

        double a = 6377563.396, b = 6356256.909;              // Airy 1830 major & minor semi-axes
        double F0 = 0.9996012717;                             // NatGrid scale factor on central meridian
        double φ0 = 49*Math.PI/180, λ0 = -2*Math.PI/180;      // NatGrid true origin
        double N0 = -100000, E0 = 400000;                     // northing & easting of true origin, metres
        double e2 = 1 - (b*b)/(a*a);                          // eccentricity squared
        double n = (a-b)/(a+b), n2 = n*n, n3 = n*n*n;         // n, n², n³

        double φ=φ0, M=0;
        do {
            φ = (N-N0-M)/(a*F0) + φ;

            double Ma = (1 + n + (5/4)*n2 + (5/4)*n3) * (φ-φ0);
            double Mb = (3*n + 3*n*n + (21/8)*n3) * Math.sin(φ-φ0) * Math.cos(φ+φ0);
            double Mc = ((15/8)*n2 + (15/8)*n3) * Math.sin(2*(φ-φ0)) * Math.cos(2*(φ+φ0));
            double Md = (35/24)*n3 * Math.sin(3*(φ-φ0)) * Math.cos(3*(φ+φ0));
            M = b * F0 * (Ma - Mb + Mc - Md);              // meridional arc

        } while (N-N0-M >= 0.00001);  // ie until < 0.01mm

        double cosφ = Math.cos(φ), sinφ = Math.sin(φ);
        double ν = a*F0/Math.sqrt(1-e2*sinφ*sinφ);            // nu = transverse radius of curvature
        double ρ = a*F0*(1-e2)/Math.pow(1-e2*sinφ*sinφ, 1.5); // rho = meridional radius of curvature
        double η2 = ν/ρ-1;                                    // eta = ?

        double tanφ = Math.tan(φ);
        double tan2φ = tanφ*tanφ, tan4φ = tan2φ*tan2φ, tan6φ = tan4φ*tan2φ;
        double secφ = 1/cosφ;
        double ν3 = ν*ν*ν, ν5 = ν3*ν*ν, ν7 = ν5*ν*ν;
        double VII = tanφ/(2*ρ*ν);
        double VIII = tanφ/(24*ρ*ν3)*(5+3*tan2φ+η2-9*tan2φ*η2);
        double IX = tanφ/(720*ρ*ν5)*(61+90*tan2φ+45*tan4φ);
        double X = secφ/ν;
        double XI = secφ/(6*ν3)*(ν/ρ+2*tan2φ);
        double XII = secφ/(120*ν5)*(5+28*tan2φ+24*tan4φ);
        double XIIA = secφ/(5040*ν7)*(61+662*tan2φ+1320*tan4φ+720*tan6φ);

        double dE = (E-E0), dE2 = dE*dE, dE3 = dE2*dE, dE4 = dE2*dE2, dE5 = dE3*dE2, dE6 = dE4*dE2, dE7 = dE5*dE2;
        φ = φ - VII*dE2 + VIII*dE4 - IX*dE6;
        double λ = λ0 + X*dE - XI*dE3 + XII*dE5 - XIIA*dE7;

        return new LatLonE(Math.toDegrees(φ), Math.toDegrees(λ), LatLonDatum.OSGB36);        
    }
    
    
    /**
     * Return as a lat lon point in WGS84
     */
    public LatLonE toLotLon() {
        return toLotLonOSGB36().convertDatum(LatLonDatum.WGS84);
    }

}
