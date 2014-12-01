/******************************************************************
 * File:        Vector3D.java
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

public class Vector3d {
    double x, y, z;
    
    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vector3d plus(Vector3d v) {
        return new Vector3d(x + v.x, y + v.y, z + v.z);
    }
    
    public Vector3d minus(Vector3d v) {
        return new Vector3d(x - v.x, y - v.y, z - v.z);
    }
    
    public Vector3d times(double scale) {
        return new Vector3d(x * scale, y * scale, z * scale);
    }
    
    public Vector3d negate() {
        return new Vector3d(-x, -y, -z);
    }
    
    public double dot(Vector3d v) {
        return x*v.x + y*v.y + z*v.z;
    }
    
    public Vector3d cross(Vector3d v) {
        double _x = this.y*v.z - this.z*v.y;
        double _y = this.z*v.x - this.x*v.z;
        double _z = this.x*v.y - this.y*v.x;

        return new Vector3d(_x, _y, _z);
    }
    
    public double length() {
        return Math.sqrt( x*x + y*y + z*z );
    }
    
    /**
     * Normalize vector to its unit length vector
     */
    public Vector3d unit() {
        double norm = this.length();
        if (norm == 1) return this;
        if (norm == 0) return this;

        return new Vector3d(x/norm, y/norm, z/norm);
    }

    /**
     * Calculates the angle between ‘this’ vector and supplied vector.
     *
     * @param   {Vector3d} v
     * @param   {Vector3d} [vSign] - If supplied (and out of plane of this and v), angle is signed +ve if
     *     this->v is clockwise looking along vSign, -ve in opposite direction (otherwise unsigned angle).
     * @returns {number} Angle (in radians) between this vector and supplied vector.
     */
    public double angleTo(Vector3d v, Vector3d vSign) {
        double sinθ = this.cross(v).length();
        double cosθ = this.dot(v);

        sinθ = this.cross(v).dot(vSign)<0 ? -sinθ : sinθ;

        return Math.atan2(sinθ, cosθ);
    };

    /**
     * Calculates the angle between ‘this’ vector and supplied vector.
     * @returns {number} Angle (in radians) between this vector and supplied vector.
     */
    public double angleTo(Vector3d v) {
        double sinθ = this.cross(v).length();
        double cosθ = this.dot(v);

        return Math.atan2(sinθ, cosθ);
    };

    /**
     * Rotates ‘this’ point around an axis by a specified angle.
     *
     * @param   {Vector3d} axis - The axis being rotated around.
     * @param   {number}   theta - The angle of rotation (in radians).
     * @returns {Vector3d} The rotated point.
     */
    public Vector3d rotateAround(Vector3d axis, double theta) {
        // en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle
        // en.wikipedia.org/wiki/Quaternions_and_spatial_rotation#Quaternion-derived_rotation_matrix
        Vector3d p1 = this.unit();
        double[] p = new double[]{p1.x, p1.y, p1.z}; // the point being rotated
        Vector3d a = axis.unit();          // the axis being rotated around
        double s = Math.sin(theta);
        double c = Math.cos(theta);
        // quaternion-derived rotation matrix
        double[][] q = new double[][]{
                new double[] {a.x*a.x*(1-c) + c,     a.x*a.y*(1-c) - a.z*s, a.x*a.z*(1-c) + a.y*s},
                new double[] {a.y*a.x*(1-c) + a.z*s, a.y*a.y*(1-c) + c,     a.y*a.z*(1-c) - a.x*s},
                new double[] {a.z*a.x*(1-c) - a.y*s, a.z*a.y*(1-c) + a.x*s, a.z*a.z*(1-c) + c}
        };
        // multiply q × p
        double[] qp = new double[]{0, 0, 0};
        for (int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                qp[i] += q[i][j] * p[j];
            }
        }
        return new Vector3d(qp[0], qp[1], qp[2]);
    };

    @Override
    public String toString() {
        return String.format("[%f, %f, %f]", x, y, z);
    }

    /**
     * Applies Helmert transform to ‘this’ point using transform parameters t.
     */
    public Vector3d applyTransform( HelmertTransform t ) {
        double x1 = this.x, y1 = this.y, z1 = this.z;
    
        double tx = t.tx, ty = t.ty, tz = t.tz;
        double rx = Math.toRadians(t.rx/3600); // normalise seconds to radians
        double ry = Math.toRadians(t.ry/3600); // normalise seconds to radians
        double rz = Math.toRadians(t.rz/3600); // normalise seconds to radians
        double s1 = t.s/1e6 + 1;             // normalise ppm to (s+1)
    
        // apply transform
        double x2 = tx + x1*s1 - y1*rz + z1*ry;
        double y2 = ty + x1*rz + y1*s1 - z1*rx;
        double z2 = tz - x1*ry + y1*rx + z1*s1;
    
        return new Vector3d(x2, y2, z2);
    };
    
    
    /**
     * Converts ‘this’ point from cartesian (x/y/z) coordinates to polar (lat/lon) coordinates on
     * specified datum.
     *
     * @param {LatLonE.datum.transform} datum - Datum to use when converting point.
     */
    public LatLonE toLatLon(LatLonDatum datum) {
        double x = this.x, y = this.y, z = this.z;
    
        double a = datum.ellipsoid.a, b = datum.ellipsoid.b;
    
        double eSq = (a*a - b*b) / (a*a);
        double p = Math.sqrt(x*x + y*y);
        double φ = Math.atan2(z, p*(1-eSq)), φʹ; // initial value of φ
        double ν;
    
        double precision = 1 / a;  // 1m: Helmert transform cannot generally do better than a few metres
        do {
            double sinφ = Math.sin(φ);
            ν = a / Math.sqrt(1 - eSq*sinφ*sinφ);
            φʹ = φ;
            φ = Math.atan2(z + eSq*ν*sinφ, p);
        } while (Math.abs(φ-φʹ) > precision);
    
        double λ = Math.atan2(y, x);
        double H = p/Math.cos(φ) - ν;
    
        return new LatLonE(Math.toDegrees(φ), Math.toDegrees(λ), datum, H);
    };    
    
}
