package com.example.atheneum.models;

/**
 * Stores Locations as a lat and lon pair.
 */
public class Location {

    private String latLng;
    private double lat;
    private double lon;

    /**
     * Creates a new Location object using default values for attributes.
     *
     * Empty constructor needed for Firebase. Do not use in application code.
     */
    public Location() {
        lat = 0.0;
        lon = 0.0;
    }

    /**
     * Creates a new Location object with specified values for lat and lon.
     *
     * @param lat Latitude of location
     * @param lon Longitude of location
     */
    public Location(String latLng, double lat, double lon) {
        this.latLng = latLng;
        this.lat = lat;
        this.lon = lon;
    }

    /**
     *
     * @return Latitude/Longitude of location
     */
    public String getLatLng() {
        return latLng;
    }

    /**
     * Updates the lat/lon of the location
     *
     * @param latLng New latitude/longitude
     */
    public void setLatLng(String latLng) {
        this.latLng = latLng;
    }

    /**
     *
     * @return Latitude of location
     */
    public double getLat() {
        return lat;
    }

    /**
     * Updates the latitude of the location
     *
     * @param lat New latitude
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     *
     * @return Longitude of the location
     */
    public double getLon() {
        return lon;
    }

    /**
     * Updates longitude of the location
     *
     * @param lon New longitude
     */
    public void setLon(double lon) {
        this.lon = lon;
    }
}
