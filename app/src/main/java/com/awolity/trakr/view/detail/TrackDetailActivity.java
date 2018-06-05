package com.awolity.trakr.view.detail;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.awolity.trakr.R;
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.viewmodel.TrackViewModel;

public class TrackDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = TrackDetailActivity.class.getSimpleName();
    public static final String TAG_MAP_FRAGMENT = "tag_map_fragment";
    public static final String TAG_DATA_FRAGMENT = "tag_data_fragment";
    public static final String TAG_CHARTS_FRAGMENT = "tag_charts_fragment";
    private static final String EXTRA_TRACK_ID = "extra_track_id";
    private static final int PERMISSION_REQUEST_CODE = 2;
    private long trackId;
    private android.support.v7.widget.Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout fragmentContainer;

    private TrackViewModel vm;

    public static Intent getStarterIntent(Context context, long trackId) {
        Intent intent = new Intent(context, TrackDetailActivity.class);
        intent.putExtra(EXTRA_TRACK_ID, trackId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupWidgets();
        setupBottomSheetNavigation();


        trackId = getIntent().getLongExtra(EXTRA_TRACK_ID, 0);
        setupViewModel(trackId);
    }

    private void setupWidgets() {
        bottomNavigationView = findViewById(R.id.navigation);
        fragmentContainer = findViewById(R.id.fragment_container);
    }

    private void setupViewModel(long trackId) {
        vm = ViewModelProviders.of(this).get(TrackViewModel.class);
        vm.init(trackId);
    }

    private void setupBottomSheetNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_map:
                        MyLog.d(LOG_TAG,"onNavigationItemSelected - action_map");
                        MapFragment mapFragment
                                = (MapFragment) getSupportFragmentManager().findFragmentByTag(TAG_MAP_FRAGMENT);
                        if (mapFragment == null) {
                            mapFragment = MapFragment.newInstance(trackId);
                        }
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, mapFragment)
                                .commit();
                        return true;
                    case R.id.action_data:

                        return true;
                    case R.id.action_charts:

                        return true;
                }
                return false;
            }
        });
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
                    vm.exportTrack();
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
           if(TrackDetailActivityUtils.checkPermission(this, PERMISSION_REQUEST_CODE)){
               vm.exportTrack();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
