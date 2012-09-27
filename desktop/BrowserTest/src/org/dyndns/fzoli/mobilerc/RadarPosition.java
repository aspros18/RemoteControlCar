package org.dyndns.fzoli.mobilerc;

/**
 *
 * @author zoli
 */
public class RadarPosition {

    private double latitude, longitude;

    public RadarPosition(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}