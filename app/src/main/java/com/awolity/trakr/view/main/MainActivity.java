package com.awolity.trakr.view.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.location.LocationManager;
import com.awolity.trakr.view.detail.TrackDetailActivity;
import com.awolity.trakr.view.list.TrackListActivity;
import com.awolity.trakr.view.main.bottom.BottomSheetBaseFragment;
import com.awolity.trakr.view.main.bottom.BottomSheetChartsFragment;
import com.awolity.trakr.view.main.bottom.BottomSheetFragmentPagerAdapter;
import com.awolity.trakr.view.main.bottom.BottomSheetPointFragment;
import com.awolity.trakr.view.main.bottom.BottomSheetTrackFragment;
import com.awolity.trakr.view.settings.SettingsActivity;
import com.awolity.trakr.viewmodel.LocationViewModel;
import com.awolity.trakr.viewmodel.SettingsViewModel;
import com.awolity.trakr.viewmodel.TrackViewModel;
import com.awolity.trakrutils.Constants;
import com.awolity.trakrutils.MyLog;
import com.awolity.trakrutils.Utility;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        TrackRecorderServiceManager.TrackRecorderServiceManagerListener {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final float ZOOM_LEVEL_INITIAL = 15;
    private static final String TAG = MainActivity.class.getSimpleName();

    private GoogleMap googleMap;
    private BottomSheetPointFragment pointFragment;
    private BottomSheetTrackFragment trackFragment;
    private BottomSheetChartsFragment chartsFragment;
    private FloatingActionButton fab;

    private LocationViewModel locationViewModel;
    private TrackViewModel trackViewModel;
    private SettingsViewModel settingsViewModel;
    // TODO: refactor viewmodels so that only one is for every activity

    private TrackRecorderServiceManager serviceManager;
    private MainActivityStatus status;
    private long trackId = Constants.NO_LAST_RECORDED_TRACK;

    private PolylineManager polylineManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // MyLog.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        status = new MainActivityStatus();
        fab = findViewById(R.id.fab);

        if (savedInstanceState != null) {
            status.setCameraPosition((CameraPosition) savedInstanceState.getParcelable(KEY_CAMERA_POSITION));
        }

        setupBottomSheet(savedInstanceState);
        MainActivityUtils.checkLocationPermission(this, PERMISSION_REQUEST_CODE);

        setupFab();
        setupMapFragment();
        setupLocationViewModel();

        settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
    }

    @SuppressWarnings("ConstantConditions")
    private void setupBottomSheet(Bundle savedInstanceState) {
        // MyLog.d(TAG, "setupBottomSheet");
        LinearLayout llBottomSheet = findViewById(R.id.ll_bottom_sheet);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        BottomSheetFragmentPagerAdapter adapter =
                new BottomSheetFragmentPagerAdapter(getSupportFragmentManager());

        if (savedInstanceState == null) {
            pointFragment = BottomSheetPointFragment.newInstance(getString(R.string.bottom_sheet_label_point));
            trackFragment = BottomSheetTrackFragment.newInstance(getString(R.string.bottom_sheet_label_track));
            chartsFragment = BottomSheetChartsFragment.newInstance(getString(R.string.bottom_sheet_label_charts));
        } else {
            pointFragment = (BottomSheetPointFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, BottomSheetPointFragment.class.getName());
            if (pointFragment == null) {
                pointFragment = BottomSheetPointFragment.newInstance(getString(R.string.bottom_sheet_label_point));
            }
            pointFragment.setTitle(getString(R.string.bottom_sheet_label_point));

            trackFragment = (BottomSheetTrackFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, BottomSheetTrackFragment.class.getName());
            if (trackFragment == null) {
                trackFragment = BottomSheetTrackFragment.newInstance(getString(R.string.bottom_sheet_label_track));
            }
            trackFragment.setTitle(getString(R.string.bottom_sheet_label_track));

            chartsFragment = (BottomSheetChartsFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, BottomSheetChartsFragment.class.getName());
            if (chartsFragment == null) {
                chartsFragment = BottomSheetChartsFragment.newInstance(getString(R.string.bottom_sheet_label_charts));
            }
            chartsFragment.setTitle(getString(R.string.bottom_sheet_label_charts));
        }
        adapter.setFragments(new BottomSheetBaseFragment[]{pointFragment, trackFragment, chartsFragment});
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_point_selected);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_track);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_charts);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(getResources().getDrawable(R.drawable.ic_point_selected));
                        tab.select();
                        break;
                    case 1:
                        tab.setIcon(getResources().getDrawable(R.drawable.ic_track_selected));
                        break;
                    case 2:
                        tab.setIcon(getResources().getDrawable(R.drawable.ic_charts_selected));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(getResources().getDrawable(R.drawable.ic_point));
                        break;
                    case 1:
                        tab.setIcon(getResources().getDrawable(R.drawable.ic_track));
                        break;
                    case 2:
                        tab.setIcon(getResources().getDrawable(R.drawable.ic_charts));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    if (googleMap != null) {
                        MainActivityUtils.scrollMapUp(MainActivity.this, googleMap);
                    }
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (googleMap != null) {
                        MainActivityUtils.scrollMapDown(MainActivity.this, googleMap);
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        };
        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);
    }

    private void setupFab() {
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MainActivityUtils.isLocationPermissionEnabled(MainActivity.this)) {
                    MainActivityUtils.checkLocationPermission(MainActivity.this,
                            PERMISSION_REQUEST_CODE);
                    return;
                }

                if (status.isRecording()) {
                    showStopDiag();
                } else {
                    locationViewModel.isLocationSettingsGood(
                            new LocationManager.LocationSettingsCallback() {
                        @Override
                        public void onLocationSettingsDetermined(boolean isSettingsGood) {
                            if (isSettingsGood) {
                                serviceManager.startService();
                            } else {
                                showLocationSettingsDialog();
                            }
                        }
                    });
                }
            }
        });
    }

    private void setupMapFragment() {
        // MyLog.d(TAG, "setupMapFragment");
        if (MainActivityUtils.checkPlayServices(this)) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapFragment);
            mapFragment.getMapAsync(this);
        } else {
            FrameLayout mapOverlay = findViewById(R.id.map_overlay);
            mapOverlay.setVisibility(View.INVISIBLE);
        }
    }

    private void setupLocationViewModel() {
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel.class);
    }

    private void showLocationSettingsDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.location_settings_rationale_title))
                .setMessage(getString(R.string.location_settings_rationale_description))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // take the user to the settings, where she/he can turn on GPS
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MainActivity.this.startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }

    private void setupTrackRecorderService() {
        MyLog.d(TAG, "setupTrackRecorderService");
        serviceManager = new TrackRecorderServiceManager(this);
        if (TrackRecorderServiceManager.isServiceRunning(this)) {
            MyLog.d(TAG, "setupTrackRecorderService - service is running");
            status.setContinueRecording();
            long trackId = settingsViewModel.getLastRecordedTrackId();
            if (trackId != Constants.NO_LAST_RECORDED_TRACK) {
                polylineManager = new PolylineManager(this);
                setupTrackViewModel(trackId);
                this.trackId = trackId;
                trackFragment.startTrackDataUpdate(trackId);
                chartsFragment.startTrackDataUpdate(trackId);
                status.setRecording(true);
                MainActivityUtils.startFabAnimation(fab);
            }
        } else {
            settingsViewModel.setLastRecordedTrackId(Constants.NO_LAST_RECORDED_TRACK);
        }
    }

    private void startLocationUpdates() {
        // MyLog.d(TAG, "startLocationUpdates");
        if (Utility.isLocationEnabled(this)) {
            locationViewModel.getLocation().observe(MainActivity.this, new Observer<Location>() {
                @Override
                public void onChanged(@Nullable Location location) {
                    if (location != null) {
                        updateMap(location);
                    }
                }
            });
        }
    }

    private void updateMap(Location location) {
        // MyLog.d(TAG, "updateMap");
        if (!status.isThereACameraPosition()) {
            // it is first start, so centered
            if (status.isRecording()) {
                centerTrackOnMap();
            } else {
                updateCamera(CameraPosition.fromLatLngZoom(new LatLng(location.getLatitude(),
                        location.getLongitude()), ZOOM_LEVEL_INITIAL));
            }
        }
    }

    private void stopLocationUpdates() {
        locationViewModel.stopLocation();
    }

    private void updateCamera(CameraPosition cameraPosition) {
        if (!cameraPosition.equals(status.getCameraPosition())) {
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            status.setCameraPosition(googleMap.getCameraPosition());
        }
    }

    private void updateCamera(final LatLngBounds bounds) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                status.setCameraPosition(googleMap.getCameraPosition());
            }
        }, 500);
    }

    private void setupTrackViewModel(final long trackId) {
        MyLog.d(TAG, "setupTrackViewModel");
        trackViewModel = ViewModelProviders.of(this).get(TrackViewModel.class);
        trackViewModel.reset();
        trackViewModel.init(trackId);
        if (status.isContinueRecording()) {
            trackViewModel.getTrackpointsList().observe(this, trackpointsListObserver);
        }
        trackViewModel.getActualTrackpoint().observe(this, actualTrackpointObserver);
    }

    private final Observer<List<TrackpointEntity>> trackpointsListObserver
            = new Observer<List<TrackpointEntity>>() {
        @Override
        public void onChanged(@Nullable List<TrackpointEntity> trackpointEntities) {
            if (trackpointEntities != null
                    && trackpointEntities.size() != 0
                    && polylineManager != null) {
                polylineManager.drawPolyline(googleMap,
                        MainActivityUtils.transformTrackpointsToLatLngs(trackpointEntities));
            }
            trackViewModel.getTrackpointsList().removeObserver(this);
        }
    };

    private final Observer<TrackpointEntity> actualTrackpointObserver
            = new Observer<TrackpointEntity>() {
        @Override
        public void onChanged(@Nullable TrackpointEntity trackpointEntity) {
            if (trackpointEntity != null) {
                if (polylineManager != null) {
                    polylineManager.continuePolyline(googleMap,
                            new LatLng(trackpointEntity.getLatitude(),
                                    trackpointEntity.getLongitude()));
                }
            }
        }
    };


    private void centerTrackOnMap() {
        trackViewModel.getTrack().observe(this, new Observer<TrackEntity>() {
            @Override
            public void onChanged(@Nullable TrackEntity track) {
                // if we already have a cameraposition, than it centering is unnecessary
                if (status.getCameraPosition() != null) {
                    return;
                }
                if (track != null) {
                    if (track.getNorthestPoint() != 0 || track.getSouthestPoint() != 0
                            || track.getWesternPoint() != 0 || track.getEasternPoint() != 0) {
                        LatLngBounds bounds = new LatLngBounds(
                                new LatLng(track.getSouthestPoint(), track.getWesternPoint()),
                                new LatLng(track.getNorthestPoint(), track.getEasternPoint()));
                        if (googleMap != null) {
                            updateCamera(bounds);
                            status.setCameraPosition(googleMap.getCameraPosition());
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        MyLog.d(TAG, "onNewIntent");
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationViewModel.isLocationSettingsGood(new LocationManager.LocationSettingsCallback() {
            @Override
            public void onLocationSettingsDetermined(boolean isSettingsGood) {
                if (isSettingsGood) {
                    setupTrackRecorderService();
                } else {
                    showLocationSettingsDialog();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
        if (status.isRecording() && !status.isThereACameraPosition()) {
            centerTrackOnMap();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Crashlytics.logException(e);
            // MyLog.e(TAG, e.getLocalizedMessage());
        }

        if (status.isThereACameraPosition()) {
            updateCamera(status.getCameraPosition());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    if (googleMap != null) {
                        try {
                            googleMap.setMyLocationEnabled(true);
                        } catch (SecurityException e) {
                            Crashlytics.logException(e);
                            // MyLog.e(TAG, e.getLocalizedMessage());
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_list_tracks) {
            startActivity(TrackListActivity.getStarterIntent(this));
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(SettingsActivity.getStarterIntent(this));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (googleMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, googleMap.getCameraPosition());

            getSupportFragmentManager().putFragment(
                    outState, BottomSheetPointFragment.class.getName(), pointFragment);
            getSupportFragmentManager().putFragment(
                    outState, BottomSheetTrackFragment.class.getName(), trackFragment);
            getSupportFragmentManager().putFragment(
                    outState, BottomSheetChartsFragment.class.getName(), chartsFragment);

            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onCameraMove() {
        status.setCameraPosition(googleMap.getCameraPosition());
    }

    @Override
    public void onServiceStarted(long trackId) {
        // MyLog.d(TAG, "onServiceStarted");
        MainActivityUtils.startFabAnimation(fab);
        this.trackId = trackId;
        setupTrackViewModel(trackId);
        trackFragment.startTrackDataUpdate(trackId);
        chartsFragment.startTrackDataUpdate(trackId);
        status.setRecording(true);
        polylineManager = new PolylineManager(this);
    }

    @Override
    public void onServiceStopped() {
        // MyLog.d(TAG, "onServiceStopped");
        MainActivityUtils.stopFabAnimation(fab);
        trackFragment.stopTrackDataUpdate();
        chartsFragment.stopTrackDataUpdate();
        status.setRecording(false);
        polylineManager.clearPolyline(googleMap);
        polylineManager = null;
        settingsViewModel.setLastRecordedTrackId(Constants.NO_LAST_RECORDED_TRACK);
        trackViewModel.getTrackWithPoints().observe(this, new Observer<TrackWithPoints>() {
            @Override
            public void onChanged(@Nullable TrackWithPoints trackWithPoints) {
                if (!status.isRecording()) {
                    if (trackWithPoints != null) {
                        if (trackId != Constants.NO_LAST_RECORDED_TRACK) {
                            if (trackWithPoints.getTrackPoints().size() > 1) {
                                trackViewModel.finishRecording();
                                Intent intent = TrackDetailActivity.getStarterIntent(
                                        MainActivity.this, trackId, null);
                                startActivity(intent);
                                trackViewModel.getTrackWithPoints().removeObserver(this);
                                trackViewModel.reset();
                                trackId = Constants.NO_LAST_RECORDED_TRACK;
                            }
                        }
                    }
                }
            }
        });
    }

    private void showStopDiag() {
        final View dialogView = View.inflate(this, R.layout.activity_main_dialog_stop_recording,
                null);
        final Dialog dialog = new Dialog(this, R.style.AppTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        Button continueBtn = dialog.findViewById(R.id.btn_continue);
        Button stopBtn = dialog.findViewById(R.id.btn_stop);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivityUtils.revealShow(fab, dialogView, false, dialog);
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivityUtils.revealShow(fab, dialogView, false, dialog);
                serviceManager.stopService();
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                MainActivityUtils.revealShow(fab, dialogView, true, null);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    MainActivityUtils.revealShow(fab, dialogView, false, dialog);
                    return true;
                }
                return false;
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }


}
