package com.awolity.trakr.view.main;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.model.MapPoint;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SameParameterValue")
class MainActivityUtils {

    private MainActivityUtils() {
    }

    static void scrollMapUp(Context context, GoogleMap googleMap) {
        googleMap.animateCamera(CameraUpdateFactory.scrollBy(0, getScrollBy(context)));
    }

    static void scrollMapDown(Context context, GoogleMap googleMap) {
        googleMap.animateCamera(CameraUpdateFactory.scrollBy(0, -getScrollBy(context)));
    }

    private static float getScrollBy(Context context) {
        return (context.getResources().getDimension(R.dimen.bottom_sheet_height_extended)
                - context.getResources().getDimension(R.dimen.bottom_sheet_height_collapsed))
                / 2;
    }

    static void checkLocationPermission(final Activity activity, final int permissionRequestCode) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(activity)
                        .setTitle(activity.getResources().getString(R.string.location_permission_rationale_title))
                        .setMessage(activity.getResources().getString(R.string.location_permission_rationale_description))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        permissionRequestCode);
                            }
                        })
                        .setIcon(R.mipmap.ic_launcher)
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        permissionRequestCode);
            }
        }
    }

    static boolean isLocationPermissionEnabled(final Activity activity) {
        return ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    static boolean checkPlayServices(final Context context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                new AlertDialog.Builder(context)
                        .setTitle(context.getResources().getString(R.string.location_permission_rationale_title))
                        .setMessage(context.getResources().getString(R.string.location_permission_rationale_description))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms"));
                                context.startActivity(i);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.mipmap.ic_launcher)
                        .show();
            }
            return false;
        }
        return true;
    }

    static List<LatLng> transformTrackpointsToLatLngs(List<MapPoint> mapPoints) {
        List<LatLng> latLngs = new ArrayList<>(mapPoints.size());
        for (MapPoint mapPoint : mapPoints) {
            latLngs.add(new LatLng(mapPoint.getLatitude(), mapPoint.getLongitude()));
        }
        return latLngs;
    }

    static void startFabAnimation(FloatingActionButton fab) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) fab.getDrawable();

            drawable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                @Override
                public void onAnimationEnd(Drawable drawable) {
                    ((Animatable) drawable).start();
                }
            });
            drawable.start();
        }
    }

    static void stopFabAnimation(FloatingActionButton fab) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) fab.getDrawable();
            drawable.clearAnimationCallbacks();
            drawable.stop();
        }
    }

    public static void revealShow(FloatingActionButton fab, View dialogView, boolean b, final Dialog dialog) {
        final View view = dialogView.findViewById(R.id.stop_dialog);

        int w = view.getWidth();
        int h = view.getHeight();
        int endRadius = (int) Math.hypot(w, h);

        int cx = (int) (fab.getX() + (fab.getWidth() / 2));
        int cy = (int) (fab.getY()) + fab.getHeight() + 56;

        if (b) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, endRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(300);
            revealAnimator.start();

        } else {

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(300);
            anim.start();
        }
    }
}
