package com.example.atheneum;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class LocationTest {
    private static final double EDMONTON_LAT = 53.5444;
    private static final double EDMONTON_LON = 113.4909;
    private static final double CALGARY_LAT = 51.0486;
    private static final double CALGARY_LON = 114.0708;

    private Location loc;

    @Before
    public void setUp() throws Exception {
        loc = new Location(EDMONTON_LAT, EDMONTON_LON);
    }

    @Test
    public void getLat() {
        assertEquals(loc.getLat(), EDMONTON_LAT);
    }

    @Test
    public void setLat() {
        loc.setLat(CALGARY_LAT);
        assertEquals(loc.getLat(), CALGARY_LAT);
        loc.setLat(EDMONTON_LAT);
        assertEquals(loc.getLat(), EDMONTON_LAT);
    }

    @Test
    public void getLon() {
        assertEquals(loc.getLon(), EDMONTON_LON);
    }

    @Test
    public void setLon() {
        loc.setLon(CALGARY_LON);
        assertEquals(loc.getLon(), CALGARY_LON);
        loc.setLon(EDMONTON_LON);
        assertEquals(loc.getLon(), EDMONTON_LON);
    }
}