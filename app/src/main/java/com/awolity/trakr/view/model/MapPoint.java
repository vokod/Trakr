package com.awolity.trakr.view.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

public class MapPoint {

    private double latitude, longitude;

    public MapPoint() {
    }

    public MapPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LatLng toLatLng() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MapPoint)) return false;
        MapPoint mapPoint = (MapPoint) o;
        return Double.compare(mapPoint.getLatitude(), getLatitude()) == 0 &&
                Double.compare(mapPoint.getLongitude(), getLongitude()) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(getLatitude(), getLongitude());
    }
}