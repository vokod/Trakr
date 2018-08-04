package com.awolity.trakr.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.location.Location;
import android.support.annotation.NonNull;

@SuppressWarnings("WeakerAccess")

@Entity(tableName = "trackpoint_table",
        foreignKeys =
        @ForeignKey(entity = TrackEntity.class,
                parentColumns = "track_id",
                childColumns = "track_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "track_id")})
public class TrackpointEntity {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "trackpoint_id")
    long trackpointId;
    @ColumnInfo(name = "track_id")
    long trackId;
    long time;
    double latitude;
    double longitude;
    double altitude;
    @ColumnInfo(name = "altitude_unfiltered")
    double unfilteredAltitude;
    double bearing;
    double speed;
    @ColumnInfo(name = "speed_unfiltered")
    double unfilteredSpeed;
    double accuracy;
    double distance;

    @NonNull
    public long getTrackpointId() {
        return trackpointId;
    }

    public void setTrackpointId(@NonNull long trackpointId) {
        this.trackpointId = trackpointId;
    }

    public long getTrackId() {
        return trackId;
    }

    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
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

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getUnfilteredAltitude() {
        return unfilteredAltitude;
    }

    public void setUnfilteredAltitude(double unfilteredAltitude) {
        this.unfilteredAltitude = unfilteredAltitude;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getUnfilteredSpeed() {
        return unfilteredSpeed;
    }

    public void setUnfilteredSpeed(double unfilteredSpeed) {
        this.unfilteredSpeed = unfilteredSpeed;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public static TrackpointEntity fromLocation(Location location) {
        TrackpointEntity tp = new TrackpointEntity();
        tp.setAltitude(location.getAltitude());
        tp.setAccuracy(location.getAccuracy());
        tp.setBearing(location.getBearing());
        tp.setLatitude(location.getLatitude());
        tp.setLongitude(location.getLongitude());
        tp.setSpeed(location.getSpeed() * 3.6);
        tp.setTime(location.getTime());
        tp.setUnfilteredAltitude(location.getAltitude());
        return tp;
    }

    @Override
    public String toString() {
        return "TrackpointEntity{" +
                "trackpointId=" + trackpointId +
                ", trackId=" + trackId +
                ", time=" + time +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", unfilteredAltitude=" + unfilteredAltitude +
                ", bearing=" + bearing +
                ", speed=" + speed +
                ", unfilteredSpeed=" + unfilteredSpeed +
                ", accuracy=" + accuracy +
                ", distance=" + distance +
                '}';
    }
}
