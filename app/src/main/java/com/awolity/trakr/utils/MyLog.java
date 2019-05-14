package com.awolity.trakr.utils;

import android.annotation.SuppressLint;

import com.awolity.trakr.BuildConfig;


@SuppressLint("LogNotTimber")
public class MyLog {

    private static final boolean LOG = BuildConfig.DEBUG;

    public static void i(String tag, String string) {
        if (LOG) {
            android.util.Log.i(tag, string);
            // Timber.i(string);
        }
    }

    public static void d(String tag, String string) {
        if (LOG) {
            android.util.Log.d(tag, string);
            // Timber.d(string);
        }
    }

    public static void e(String tag, String string) {
        if (LOG) {
            android.util.Log.e(tag, string);
            // Timber.e(string);
        }
    }

    public static void v(String tag, String string) {
        if (LOG) {
            android.util.Log.v(tag, string);
            // Timber.v(string);
        }
    }

    public static void w(String tag, String string) {
        if (LOG) {
            android.util.Log.w(tag, string);
            // Timber.w(string);
        }
    }

    public static void wtf(String tag, String string) {
        if (LOG) {
            android.util.Log.wtf(tag, string);
            // Timber.wtf(string);
        }
    }
}
