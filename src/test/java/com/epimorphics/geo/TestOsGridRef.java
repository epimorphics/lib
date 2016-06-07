/******************************************************************
 * File:        TestOsGridRef.java
 * Created by:  Dave Reynolds
 * Created on:  1 Dec 2014
 * 
 * (c) Copyright 2014, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.geo;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestOsGridRef {

    @Test
    public void testParseFormat() {
        OsGridRef ref = OsGridRef.parse("TL 85090 84360");
        OsGridRef ref2 = new OsGridRef(12,24);
        
        assertEquals(585090, ref.getEasting());
        assertEquals(284360, ref.getNorthing());
        assertEquals("TL 85090 84360", ref.format(10));
        assertEquals("TL 8509 8436", ref.format(8));
        assertEquals("TL 85 84", ref.format(4));
        
        assertEquals("TL 85090 84360", new OsGridRef(585090, 284360).format(10));

        // Round trip testing of grid ref parser and formatter
        assertEquals(ref2.format(10), OsGridRef.parse(ref2.format(10)).format(10));

        ref = OsGridRef.parse("TA0539739744");
        assertEquals(505397, ref.getEasting());
        assertEquals(439744, ref.getNorthing());
        
        ref = OsGridRef.parse("TA 05 39");
        assertEquals(505500, ref.getEasting());
        assertEquals(439500, ref.getNorthing());
    }
    
    @Test
    public void testLatLonConvert() {
        OsGridRef ref = new OsGridRef(429157, 623009);
        LatLonE point = ref.toLatLon();
        assertEquals(55.5, point.getLat(), 0.0001); 
        assertEquals(-1.54, point.getLon(), 0.0001);
        assertTrue(point.datum == LatLonDatum.WGS84);
        assertEquals(ref.format(10), OsGridRef.fromLatLon(point).format(10));
        
        ref = new OsGridRef(412345, 643210);
        point = ref.toLatLon();
        assertEquals(55.6822199717, point.getLat(), 0.0001); 
        assertEquals(-1.80523897165, point.getLon(), 0.0001);
        assertEquals(ref.format(10), OsGridRef.fromLatLon(point).format(10));
    }
    
    @Test
    public void testGeoPointWrapper() {
        GeoPoint point = GeoPoint.fromEastingNorthing(429157, 623009);
        assertEquals(55.5, point.getLat(), 0.0001); 
        assertEquals(-1.54, point.getLon(), 0.0001);
        assertEquals("NU 29157 23009", point.getGridRefString());

        point = GeoPoint.fromLatLon(55.6822199717, -1.80523897165);
        assertEquals(412345, point.getEasting(), 2);
        assertEquals(643210, point.getNorthing(), 2);
        
        assertTrue( point.getLatLiteral().asLiteral().getLexicalForm().startsWith("55.682") );
        assertTrue( point.getLonLiteral().asLiteral().getLexicalForm().startsWith("-1.805") );
    }
}
