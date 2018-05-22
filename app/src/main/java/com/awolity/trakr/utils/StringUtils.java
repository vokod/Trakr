package com.awolity.trakr.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class StringUtils {

    private StringUtils() {
    }

    public static String getLegalizedFilename(String illegalFileName) {
        return illegalFileName.replaceAll("[\\\\/:*?\"<>| ]", "");
    }

    public static String getTimeStringFromMillis(long timeInMillis) {
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());
        timeInMillis -= offsetInMillis;
        Date startTime = new Date(timeInMillis);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return df.format(startTime);
    }

    public static String getDistanceAsThreeCharactersString(double distance) {
        int lengthInt = (int) distance;
        int km = lengthInt / 1000;
        int m = lengthInt % 100000;

        if (km == 0) {
            m = m / 10;
            if (m < 10) {
                return "0.0" + m;
            } else {
                return "0." + m;
            }
        } else if (km < 10) {
            m = (m - (km * 1000)) / 10;
            if (m < 10) {
                return km + ".0" + m;
            } else {
                return km + "." + m;
            }
        } else if (km < 100) {
            m = (m - (km * 1000)) / 100;
            return km + "." + m;
        } else {
            return String.valueOf(km);
        }
    }

    public static String getSpeedAsThreeCharactersString(double speed) {
        if (speed < 100) {
            if (speed < 10) {

                return String.format(Locale.getDefault(), "%.2f", speed);
            }
            return String.format(Locale.getDefault(), "%.1f", speed);
        }
        return String.format(Locale.getDefault(), "%.0f", speed);
    }

    public static String getElapsedTimeAsString(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = seconds / 3600;
        seconds = seconds % 60;
        minutes = minutes % 60;
        if (hours == 0) {
            if (minutes == 0) {
                return "00:"
                        + String.format(Locale.getDefault(),"%02d", seconds);
            }
            return String.format(Locale.getDefault(),"%02d", minutes)
                    + ":"
                    + String.format(Locale.getDefault(),"%02d", seconds);
        }
        return String.format(Locale.getDefault(),"%02d", hours)
                + ":"
                + String.format(Locale.getDefault(),"%02d", minutes)
                + ":"
                + String.format(Locale.getDefault(),"%02d", seconds);
    }

    public static String getStartTimeAsString(long millis) {
        Date time = new Date(millis);
        SimpleDateFormat sdfDate = new SimpleDateFormat("MM-dd HH:mm");
        return sdfDate.format(time);
    }
}
