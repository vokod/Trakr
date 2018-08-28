package com.awolity.trakr.view.detail;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.awolity.trakr.R;

@SuppressWarnings("SameParameterValue")
class TrackDetailActivityUtils {

    public static final String TAG = TrackDetailActivityUtils.class.getSimpleName();

    private TrackDetailActivityUtils(){}

    static boolean checkPermission(final Activity activity, final int permissionRequestCode) {
      // MyLog.d(TAG, "checkPermission");
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(activity)
                        .setTitle(activity.getResources().getString(R.string.external_storage_permission_rationale_title))
                        .setMessage(activity.getResources().getString(R.string.external_storage_permission_rationale_description))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                              // MyLog.d(TAG, "checkPermission - shouldshowrationale - onclick - requesting permission");
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        permissionRequestCode);
                            }
                        })
                        .setIcon(R.mipmap.ic_launcher)
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        permissionRequestCode);
            }
        } else {
            return true;
        }
        return false;
    }
}
