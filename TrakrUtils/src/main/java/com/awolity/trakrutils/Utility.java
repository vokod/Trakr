package com.awolity.trakrutils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

public class Utility {

    public static boolean isLocationEnabled(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static Drawable getInitial(String firstLetter, String colorBase, int widthInPixels) {
        ColorGenerator generator = ColorGenerator.MATERIAL;
        return TextDrawable.builder()
                .beginConfig()
                .width(widthInPixels)  // width in px
                .height(widthInPixels) // height in px
                .endConfig()
                .buildRound(firstLetter, generator.getColor(colorBase));
    }

    public static void showToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }



}
