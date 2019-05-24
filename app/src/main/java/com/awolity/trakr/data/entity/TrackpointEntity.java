package com.awolity.trakr.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import android.location.Location;

import androidx.annotation.NonNull;

@SuppressWarnings("WeakerAccess")
@Entity(tableName = "trackpoint_table",
        foreignKeys =
        @ForeignKey(entity = TrackEntity.class,
                parentColumns = "track_id",
                childColumns = "track_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "track_id")})
public class TrackpointEntity {

    @SuppressWarnings("NullableProblems")
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
    @Ignore
    double unfilteredAltitude;
    @Ignore
    double bearing;
    double speed;
    @Ignore
    double unfilteredSpeed;
    @Ignore
    double accuracy;
    double distance;

    public long getTrackpointId() {
        return trackpointId;
    }

    public void setTrackpointId(long trackpointId) {
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
        tp.setUnfilteredSpeed(location.getSpeed() * 3.6);
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
                ", pointAltitudes=" + altitude +
                ", unfilteredAltitude=" + unfilteredAltitude +
                ", bearing=" + bearing +
                ", pointSpeeds=" + speed +
                ", unfilteredSpeed=" + unfilteredSpeed +
                ", accuracy=" + accuracy +
                ", distance=" + distance +
                '}';
    }
}
