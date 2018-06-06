package com.awolity.trakr.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.awolity.trakr.utils.MyLog;

import java.util.Calendar;

@SuppressWarnings("WeakerAccess")
@Entity(tableName = "track_table")
public class TrackEntity {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "track_id")
    long trackId;
    String title;
    @ColumnInfo(name = "start_time")
    long startTime;
    double distance;
    double ascent;
    double descent;
    @ColumnInfo(name = "elapsed_time")
    long elapsedTime;
    @ColumnInfo(name = "num_of_trackpoints")
    long numOfTrackPoints;
    @ColumnInfo(name = "northest_point")
    double northestPoint;
    @ColumnInfo(name = "southest_point")
    double southestPoint;
    @ColumnInfo(name = "western_point")
    double westernPoint;
    @ColumnInfo(name = "eastern_point")
    double easternPoint;
    @ColumnInfo(name = "lowest_point")
    double minAltitude;
    @ColumnInfo(name = "highest_point")
    double maxAltitude;
    @ColumnInfo(name = "max_speed")
    double maxSpeed;
    @ColumnInfo(name = "avg_speed")
    double avgSpeed;
    String metadata;
    @Ignore
    private boolean isValidElevationData = false;
    @Ignore
    private static final String TAG = TrackEntity.class.getSimpleName();

    @NonNull
    public long getTrackId() {
        return trackId;
    }

    public void setTrackId(@NonNull long trackId) {
        this.trackId = trackId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getAscent() {
        return ascent;
    }

    public void setAscent(double ascent) {
        this.ascent = ascent;
    }

    public double getDescent() {
        return descent;
    }

    public void setDescent(double descent) {
        this.descent = descent;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public long getNumOfTrackPoints() {
        return numOfTrackPoints;
    }

    public void setNumOfTrackPoints(long numOfTrackPoints) {
        this.numOfTrackPoints = numOfTrackPoints;
    }

    public double getNorthestPoint() {
        return northestPoint;
    }

    public void setNorthestPoint(double northestPoint) {
        this.northestPoint = northestPoint;
    }

    public double getSouthestPoint() {
        return southestPoint;
    }

    public void setSouthestPoint(double southestPoint) {
        this.southestPoint = southestPoint;
    }

    public double getWesternPoint() {
        return westernPoint;
    }

    public void setWesternPoint(double westernPoint) {
        this.westernPoint = westernPoint;
    }

    public double getEasternPoint() {
        return easternPoint;
    }

    public void setEasternPoint(double easternPoint) {
        this.easternPoint = easternPoint;
    }

    public double getMaxAltitude() {
        return minAltitude;
    }

    public void setMinAltitude(double minAltitude) {
        this.minAltitude = minAltitude;
    }

    public double getMinAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(double maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
        if (maxSpeed == 0) {
            setMaxSpeed(avgSpeed);
        }
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public void increaseDistance(double delta) {
        distance += delta;
    }

    public void increaseAscent(double delta) {
        ascent += delta;
    }

    public void increaseDescent(double delta) {
        descent += delta;
    }

    public void increaseElapsedTime(long newTime) {
        elapsedTime = newTime - startTime;
        if (elapsedTime < 0) {
            MyLog.wtf(TAG, "WTF, elapsed time is less than 0!!!");
        }
    }

    public void increaseNumOfTrackpoints() {
        numOfTrackPoints++;
    }

    public void calculateAvgSpeed() {
        setAvgSpeed(getDistance() / ((double) getElapsedTime() / 1000) * 3.6);
    }

    public void calculateAscentDescent(TrackpointEntity actualTp, TrackpointEntity previousTp) {
        if (previousTp != null) {
            if (actualTp.getAltitude() > previousTp.getAltitude()) {
                // felfelé megyünk
                increaseAscent(actualTp.getAltitude() - previousTp.getAltitude());
            } else {
                // lefelé megyünk, vagy egyenlő a magasságunk
                increaseDescent(previousTp.getAltitude() - actualTp.getAltitude());
            }
        }
    }

    public void checkSetExtremeValues(TrackpointEntity tp) {
        checkSetNorth(tp.getLatitude());
        checkSetSouth(tp.getLatitude());
        checkSetWest(tp.getLongitude());
        checkSetEast(tp.getLongitude());
        checkSetHighLow(tp.getAltitude());
        checkSetMaxSpeed(tp.getSpeed());
    }

    public void checkSetHighLow(double altitude) {
        if (!isValidElevationData) {
            // there was no valid elevation data before, only 0-s
            if (altitude != 0) {
                // this one is not 0 finally
                maxAltitude = altitude;
                minAltitude = altitude;
                isValidElevationData = true;
            }
        } else {
            // we already had valid elevation data
            if (altitude > maxAltitude) {
                maxAltitude = altitude;
            }
            if (altitude < minAltitude) {
                minAltitude = altitude;
            }
        }
    }

    private void checkSetNorth(double north) {
        if (north > northestPoint || northestPoint == 0) {
            northestPoint = north;
        }
    }

    private void checkSetSouth(double south) {
        if (southestPoint > south || southestPoint == 0) {
            southestPoint = south;
        }
    }

    private void checkSetWest(double west) {
        if (west < westernPoint || westernPoint == 0) {
            westernPoint = west;
        }
    }

    private void checkSetEast(double east) {
        if (east > easternPoint || easternPoint == 0) {
            easternPoint = east;
        }
    }

    private void checkSetMaxSpeed(double maxSpeed) {
        if (this.maxSpeed < maxSpeed) {
            this.maxSpeed = maxSpeed;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrackEntity)) return false;

        TrackEntity that = (TrackEntity) o;

        if (getTrackId() != that.getTrackId()) return false;
        if (getStartTime() != that.getStartTime()) return false;
        if (Double.compare(that.getDistance(), getDistance()) != 0) return false;
        if (Double.compare(that.getAscent(), getAscent()) != 0) return false;
        if (Double.compare(that.getDescent(), getDescent()) != 0) return false;
        if (getElapsedTime() != that.getElapsedTime()) return false;
        if (getNumOfTrackPoints() != that.getNumOfTrackPoints()) return false;
        if (Double.compare(that.getNorthestPoint(), getNorthestPoint()) != 0) return false;
        if (Double.compare(that.getSouthestPoint(), getSouthestPoint()) != 0) return false;
        if (Double.compare(that.getWesternPoint(), getWesternPoint()) != 0) return false;
        if (Double.compare(that.getEasternPoint(), getEasternPoint()) != 0) return false;
        if (Double.compare(that.getMinAltitude(), getMinAltitude()) != 0) return false;
        if (Double.compare(that.getMaxAltitude(), getMaxAltitude()) != 0) return false;
        if (Double.compare(that.getMaxSpeed(), getMaxSpeed()) != 0) return false;
        if (Double.compare(that.getAvgSpeed(), getAvgSpeed()) != 0) return false;
        if (getTitle() != null ? !getTitle().equals(that.getTitle()) : that.getTitle() != null)
            return false;
        return getMetadata() != null ? getMetadata().equals(that.getMetadata()) : that.getMetadata() == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (getTrackId() ^ (getTrackId() >>> 32));
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (int) (getStartTime() ^ (getStartTime() >>> 32));
        temp = Double.doubleToLongBits(getDistance());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getAscent());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getDescent());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (getElapsedTime() ^ (getElapsedTime() >>> 32));
        result = 31 * result + (int) (getNumOfTrackPoints() ^ (getNumOfTrackPoints() >>> 32));
        temp = Double.doubleToLongBits(getNorthestPoint());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getSouthestPoint());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getWesternPoint());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getEasternPoint());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getMinAltitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getMaxAltitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getMaxSpeed());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getAvgSpeed());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (getMetadata() != null ? getMetadata().hashCode() : 0);
        return result;
    }

    public static String getDefaultName(long startTime) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(startTime);
        int hour = cl.get(Calendar.HOUR_OF_DAY);
        if (hour <= 3 || hour > 21) {
            return "Night track";
        } else if(hour <= 6){
            return "Dawn track";
        } else if (hour <= 8) {
            return "Early morning track";
        } else if (hour <= 12) {
            return "Morning track";
        } else if ( hour <= 18) {
            return "Afternoon track";
        } else {
            return "Evening track";
        }
    }
}
