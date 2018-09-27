package com.awolity.trakr.model;

import com.awolity.trakrutils.Constants;

import java.util.Objects;

public class ChartPoint {
    private long time;
    private double altitude;
    private double speed;
    private double distance;

    public ChartPoint(){}

    public ChartPoint(long time, double altitude, double speed, double distance){
        this.time = time;
        this.altitude = altitude;
        this.speed = speed;
        this.distance = distance;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void convertToImperial(){
        altitude = altitude / Constants.FOOT;
        speed = speed / Constants.MILE;
        distance = distance / Constants.MILE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChartPoint)) return false;
        ChartPoint that = (ChartPoint) o;
        return getTime() == that.getTime() &&
                Double.compare(that.getAltitude(), getAltitude()) == 0 &&
                Double.compare(that.getSpeed(), getSpeed()) == 0 &&
                Double.compare(that.getDistance(), getDistance()) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(getTime(), getAltitude(), getSpeed(), getDistance());
    }
}
