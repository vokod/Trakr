package com.awolity.trakrutils;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StringUtils {

    private StringUtils() {
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

    public static String getPaceAsString(double speed) {
        double pace = (60 * (1 / speed));
        long iPart;
        double fPart;
        iPart = (long) pace;
        fPart = pace - iPart;
        fPart = fPart * 0.6;
        int fPartInt = (int) (fPart * 100);
        if ((fPartInt - 10) < 0) {
            return String.valueOf(iPart) + ":" + "0" + String.valueOf(fPartInt);
        } else {
            return String.valueOf(iPart) + ":" + String.valueOf(fPartInt);
        }
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
                        + String.format(Locale.getDefault(), "%02d", seconds);
            }
            return String.format(Locale.getDefault(), "%02d", minutes)
                    + ":"
                    + String.format(Locale.getDefault(), "%02d", seconds);
        }
        return String.format(Locale.getDefault(), "%02d", hours)
                + ":"
                + String.format(Locale.getDefault(), "%02d", minutes)
                + ":"
                + String.format(Locale.getDefault(), "%02d", seconds);
    }

    public static String getTimeAsString(long millis) {
        java.util.Date time = new java.util.Date(millis);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm");
        return sdfDate.format(time);
    }

    public static String getDateAsStringLocale(long millis) {
        Date date = new Date(millis);
        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance(DateFormat.DEFAULT);
        return dateFormat.format(date);
    }
}
