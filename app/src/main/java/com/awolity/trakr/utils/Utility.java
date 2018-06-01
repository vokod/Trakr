package com.awolity.trakr.utils;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

import com.awolity.trakr.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

public class Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static Bitmap getBitmap(int drawableRes, Context context) {
        Drawable drawable = context.getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static boolean isLocationEnabled(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void scrollMapUp(Context context, GoogleMap googleMap) {
        googleMap.animateCamera(CameraUpdateFactory.scrollBy(0, getScrollBy(context)));
    }

    public static void scrollMapDown(Context context, GoogleMap googleMap) {

        googleMap.animateCamera(CameraUpdateFactory.scrollBy(0, -getScrollBy(context)));
    }

    private static float getScrollBy(Context context) {
        return (context.getResources().getDimension(R.dimen.bottom_sheet_height_extended)
                - context.getResources().getDimension(R.dimen.bottom_sheet_height_collapsed))
                / 2;
    }


}
