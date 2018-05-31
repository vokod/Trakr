package com.awolity.trakr.view.detail;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.awolity.trakr.R;
import com.awolity.trakr.databinding.ActivityTrackDetailBinding;
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.viewmodel.TrackViewModel;

public class TrackDetailActivity extends AppCompatActivity {


    private static final String LOG_TAG = TrackDetailActivity.class.getSimpleName();
    private static final String EXTRA_TRACK_ID = "extra_track_id";
    private static final int PERMISSION_REQUEST_CODE = 2;

    private TrackViewModel vm;
    private ActivityTrackDetailBinding binding;

    public static Intent getStarterIntent(Context context, long trackId) {
        Intent intent = new Intent(context, TrackDetailActivity.class);
        intent.putExtra(EXTRA_TRACK_ID, trackId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_track_detail);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_track_detail);
        setupToolbar();

        BottomNavigationView navigation = binding.navigation;
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setupViewModel(getIntent().getLongExtra(EXTRA_TRACK_ID, 0));
    }

    private void setupToolbar() {
        android.support.v7.widget.Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
    }

    private void setupViewModel(long trackId) {
        vm = ViewModelProviders.of(this).get(TrackViewModel.class);
        vm.init(trackId);
    }

    private void checkPermission() {
        MyLog.d(LOG_TAG, "checkPermission");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            MyLog.d(LOG_TAG, "checkPermission - permission not granted");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                MyLog.d(LOG_TAG, "checkPermission - shouldshowrationale - should");
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.external_storage_permission_rationale_title))
                        .setMessage(getResources().getString(R.string.external_storage_permission_rationale_description))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // MyLog.d(LOG_TAG, "checkPermission - shouldshowrationale - onclick - requesting permission");
                                ActivityCompat.requestPermissions(TrackDetailActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        PERMISSION_REQUEST_CODE);
                            }
                        })
                        .setIcon(R.mipmap.ic_launcher)
                        .show();
            } else {
                MyLog.d(LOG_TAG, "checkPermission - shouldshowrationale - no - requesting permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            }
        } else {
            MyLog.d(LOG_TAG, "checkPermission - permission granted");
            vm.exportTrack();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        MyLog.d(LOG_TAG, "onRequestPermissionsResult");

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.d(LOG_TAG, "onRequestPermissionsResult - permission granted");
                    // permission was granted, yay!
                } else {
                    MyLog.d(LOG_TAG, "onRequestPermissionsResult - permission denied :(");
                    // permission denied, boo!
                    Toast.makeText(this, getString(R.string.write_permission_denied),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.track_detail_activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            vm.deleteTrack();
            finish();
            return true;
        } else if (id == R.id.action_export) {
            checkPermission();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_map:

                    return true;
                case R.id.action_data:

                    return true;
                case R.id.action_charts:

                    return true;
            }
            return false;
        }
    };


}
