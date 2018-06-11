package com.awolity.trakr.view.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivityUtils {

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

    static List<LatLng> transformTrackpointsToLatLngs(List<TrackpointEntity> trackpoints) {
        List<LatLng> latLngs = new ArrayList<>(trackpoints.size());
        for (TrackpointEntity trackpoint : trackpoints) {
            latLngs.add(new LatLng(trackpoint.getLatitude(), trackpoint.getLongitude()));
        }
        return latLngs;
    }

    static void showToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }
}
