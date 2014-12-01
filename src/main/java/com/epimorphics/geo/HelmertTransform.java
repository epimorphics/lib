/******************************************************************
 * File:        HelmertTransform.java
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

public class HelmertTransform {
    double tx;   // in m
    double ty;   // in m
    double tz;   // in m
    double rx;   // in sec
    double ry;   // in sec
    double rz;   // in sec
    double s;    // ppm
    
    public HelmertTransform(double tx, double ty, double tz, double rx, double ry, double rz, double s) {
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
        
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        
        this.s = s;
    }

    public double getTx() {
        return tx;
    }

    public double getTy() {
        return ty;
    }

    public double getTz() {
        return tz;
    }

    public double getRx() {
        return rx;
    }

    public double getRy() {
        return ry;
    }

    public double getRz() {
        return rz;
    }

    public double getS() {
        return s;
    }
    
    public HelmertTransform inverse() {
        return new HelmertTransform(-tx, -ty, -tz, -rx, -ry, -rz, -s);
    }
}
