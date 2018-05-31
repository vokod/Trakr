package com.awolity.trakr.view.main;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.databinding.ActivityMainBinding;
import com.awolity.trakr.location.LocationManager;
import com.awolity.trakr.trackrecorder.TrackRecorderServiceManager;
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.utils.PreferenceUtils;
import com.awolity.trakr.utils.Utility;
import com.awolity.trakr.view.SettingsActivity;
import com.awolity.trakr.view.list.TrackListActivity;
import com.awolity.trakr.viewmodel.LocationViewModel;
import com.awolity.trakr.viewmodel.TrackViewModel;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ms_square.debugoverlay.DebugOverlay;
import com.ms_square.debugoverlay.Position;
import com.ms_square.debugoverlay.modules.LogcatModule;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        TrackRecorderServiceManager.TrackRecorderServiceManagerListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final float ZOOM_LEVEL_INITIAL = 15;

    private ActivityMainBinding binding;
    private GoogleMap googleMap;
    private BottomSheetPointFragment pointFragment;
    private BottomSheetTrackFragment trackFragment;
    private BottomSheetChartsFragment chartsFragment;
    private LocationViewModel locationViewModel;
    private TrackRecorderServiceManager serviceManager;
    private MainActivityStatus status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  // MyLog.d(TAG, "onCreate");
        status = new MainActivityStatus();

        if (savedInstanceState != null) {
            status.setCameraPosition((CameraPosition) savedInstanceState.getParcelable(KEY_CAMERA_POSITION));
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setupToolbar();
        setupBottomSheet(savedInstanceState);
        checkPermission();
        setupMapFragment();
        setupLocationViewModel();
        setupDebugOverlay();
    }

    private void setupToolbar() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
    }

    @SuppressWarnings("ConstantConditions")
    private void setupBottomSheet(Bundle savedInstanceState) {
        //  // MyLog.d(TAG, "setupBottomSheet");
        LinearLayout llBottomSheet = binding.llBottomSheet;
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        BottomSheetFragmentPagerAdapter adapter =
                new BottomSheetFragmentPagerAdapter(getSupportFragmentManager());
        // TODO: extract resources
        if (savedInstanceState == null) {
            pointFragment = BottomSheetPointFragment.newInstance("Point");
            //  // MyLog.d(TAG, "setupBottomSheet - pointfragment: " + pointFragment.hashCode());
            trackFragment = BottomSheetTrackFragment.newInstance("Track");
            //  // MyLog.d(TAG, "setupBottomSheet - trackFragment: " + trackFragment.hashCode());
            chartsFragment = BottomSheetChartsFragment.newInstance("Charts");
            //  // MyLog.d(TAG, "setupBottomSheet - chartsFragment: " + chartsFragment.hashCode());
        } else {
            pointFragment = (BottomSheetPointFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, BottomSheetPointFragment.class.getName());
            pointFragment.setTitle("Point");
            //  // MyLog.d(TAG, "setupBottomSheet - pointfragment: " + pointFragment.hashCode());
            trackFragment = (BottomSheetTrackFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, BottomSheetTrackFragment.class.getName());
            trackFragment.setTitle("Track");
            //  // MyLog.d(TAG, "setupBottomSheet - trackFragment: " + trackFragment.hashCode());
            chartsFragment = (BottomSheetChartsFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, BottomSheetChartsFragment.class.getName());
            chartsFragment.setTitle("Charts");
            //  // MyLog.d(TAG, "setupBottomSheet - chartsFragment: " + chartsFragment.hashCode());
        }
        adapter.setFragments(new BottomSheetBaseFragment[]{pointFragment, trackFragment, chartsFragment});
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOffscreenPageLimit(2);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.tabLayout.getTabAt(0).setIcon(R.drawable.ic_point_selected);
        binding.tabLayout.getTabAt(1).setIcon(R.drawable.ic_track);
        binding.tabLayout.getTabAt(2).setIcon(R.drawable.ic_charts);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(getResources().getDrawable(R.drawable.ic_point_selected));
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
                        // TODO: move camera
                    }
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    // TODO: move camera
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        };
        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);
    }

    private void checkPermission() {
        // //  // MyLog.d(LOG_TAG, "checkPermission");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // //  // MyLog.d(LOG_TAG, "checkPermission - permission not granted");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // //  // MyLog.d(LOG_TAG, "checkPermission - shouldshowrationale - should");
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.location_permission_rationale_title))
                        .setMessage(getResources().getString(R.string.location_permission_rationale_description))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // //  // MyLog.d(LOG_TAG, "checkPermission - shouldshowrationale - onclick - requesting permission");
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSION_REQUEST_CODE);
                            }
                        })
                        .setIcon(R.mipmap.ic_launcher)
                        .show();
            } else {
                // //  // MyLog.d(LOG_TAG, "checkPermission - shouldshowrationale - no - requesting permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_CODE);
            }
        } else {
            // //  // MyLog.d(LOG_TAG, "checkPermission - permission granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        //  // MyLog.d(TAG, "onRequestPermissionsResult");

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //  // MyLog.d(TAG, "onRequestPermissionsResult - permission granted");
                    // permission was granted, yay!
                    // TODO: ?
                } else {
                    //  // MyLog.d(TAG, "onRequestPermissionsResult - permission denied :(");
                    // permission denied, boo!
                    // TODO: ?
                }
            }
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.location_permission_rationale_title))
                        .setMessage(getResources().getString(R.string.location_permission_rationale_description))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms"));
                                startActivity(i);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.mipmap.ic_launcher)
                        .show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    private void setupMapFragment() {
        if (checkPlayServices()) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapFragment);
            mapFragment.getMapAsync(this);
        } else {
            FrameLayout mapOverlay = binding.mapOverlay;
            mapOverlay.setVisibility(View.INVISIBLE);
        }
    }

    private void setupLocationViewModel() {
        //  // MyLog.d(TAG, "setupLocationViewModel");
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel.class);
        locationViewModel.isLocationSettingsGood(new LocationManager.LocationSettingsCallback() {
            @Override
            public void onLocationSettingsDetermined(boolean isSettingsGood) {
                //  // MyLog.d(TAG, "setupLocationViewModel - onLocationSettingsDetermined");
                if (!isSettingsGood) {
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
            }
        });
    }

    private void setupDebugOverlay() {
        new DebugOverlay.Builder(this.getApplication())
                .modules(new LogcatModule())
                .position(Position.TOP_START)
                .allowSystemLayer(true)
                .notification(true, MainActivity.class.getName())
                .build()
                .install();
    }

    private void setupTrackRecorderService() {
        //  // MyLog.d(TAG, "setupTrackRecorderService");
        serviceManager = new TrackRecorderServiceManager(this);
        if (TrackRecorderServiceManager.isServiceRunning(this)) {
            status.setContinueRecording();
            long trackId = PreferenceUtils.getLastRecordedTrackId(this);
            //  // MyLog.d(TAG, "setupTrackRecorderService - service is running. TrackId: " + trackId);
            if (trackId != PreferenceUtils.NO_LAST_RECORDED_TRACK) {
                setupTrackViewModel(trackId);
                trackFragment.start(trackId);
                status.setRecording(true);
            } else {
                //  // MyLog.wtf(TAG, "A track is recorded, but it's ID is unknown!!!");
            }
        } else {
            //  // MyLog.d(TAG, "setupTrackRecorderService - service is not running");
        }
    }

    @Override
    protected void onStart() {
        //  // MyLog.d(TAG, "onStart");
        super.onStart();
        setupTrackRecorderService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  // MyLog.d(TAG, "onResume");
        startLocationUpdates();
        if (status.isRecording() && !status.isThereACameraPosition()) {
            //  // MyLog.d(TAG, "onResume - centerTrackOnMap");
            centerTrackOnMap();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //  // MyLog.d(TAG, "onPause");
        stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //  // MyLog.d(TAG, "onStop");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //  // MyLog.d(TAG, "onMapReady");
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
             // MyLog.e(TAG, e.getLocalizedMessage());
        }

        if (status.isThereACameraPosition()) {
            updateCamera(status.getCameraPosition());
        }
    }

    private void startLocationUpdates() {
        if (Utility.isLocationEnabled(this)) {
            //  // MyLog.d(TAG, "startLocationUpdates");
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
        if (!status.isThereACameraPosition()) {
            // it is first start, so centered
            if (status.isRecording()) {
                //  // MyLog.d(TAG, "updateMap - no last position, recording");
                centerTrackOnMap();
            } else {
                //  // MyLog.d(TAG, "updateMap - no last position, no recording");
                updateCamera(CameraPosition.fromLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), ZOOM_LEVEL_INITIAL));
            }
        }
    }

    private void stopLocationUpdates() {
        locationViewModel.stopLocation();
    }

    private void updateCamera(CameraPosition cameraPosition) {
        //  // MyLog.d(TAG, "updateCamera");
        if (!cameraPosition.equals(status.getCameraPosition())) {
            //  // MyLog.d(TAG, "updateCamera - actual camera position equals saved");
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            status.setCameraPosition(googleMap.getCameraPosition());
        }
    }

    private void updateCamera(LatLngBounds bounds) {
        //  // MyLog.d(TAG, "updateCamera");

        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        status.setCameraPosition(googleMap.getCameraPosition());
    }

    protected void onRecordFabClick(@SuppressWarnings("unused") View view) {
        //  // MyLog.d(TAG, "onRecordFabClick");
        serviceManager.startStopFabClicked();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_list_tracks) {
            startActivity(TrackListActivity.getStarterIntent(this));
            return true;
        } else if (id == R.id.action_crash) {
            Crashlytics.getInstance().crash();
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
        //  // MyLog.d(TAG, "onCameraMove");
        status.setCameraPosition(googleMap.getCameraPosition());
    }

    @Override
    public void onServiceStarted(long trackId) {
        //  // MyLog.d(TAG, "onServiceStarted");
        // TODO FAB animation
        trackFragment.start(trackId);
        chartsFragment.start(trackId);
        setupTrackViewModel(trackId);
        status.setRecording(true);
    }

    @Override
    public void onServiceStopped() {
        //  // MyLog.d(TAG, "onServiceStopped");
        trackFragment.stop();
        chartsFragment.stop();
        // TODO: status.stoprecording (ami aztán megcsinálja mindkettőt)
        status.setRecording(false);
        clearTrackOnMap();
    }

    @SuppressWarnings("FieldCanBeLocal")
    private TrackViewModel trackViewModel;

    private void setupTrackViewModel(final long trackId) {
        //  // MyLog.d(TAG, "setupTrackViewModel - trackId = :" + trackId);
        trackViewModel = ViewModelProviders.of(this).get(TrackViewModel.class);
        trackViewModel.init(trackId);
        if (status.isContinueRecording()) {
            trackViewModel.getTrackpointsList().observe(this, trackpointsListObserver);
        }
        trackViewModel.getActualTrackpoint().observe(this, actualTrackpointObserver);

    }

    private Observer<List<TrackpointEntity>> trackpointsListObserver = new Observer<List<TrackpointEntity>>() {
        @Override
        public void onChanged(@Nullable List<TrackpointEntity> trackpointEntities) {
             // MyLog.d(TAG, "trackpointsListObserver - onChanged");
            if (trackpointEntities != null && trackpointEntities.size() != 0) {
                 // MyLog.d(TAG, "trackpointsListObserver - onChanged - size: " + trackpointEntities.size());
                drawTrackOnMap(transformTrackpointsToLatLngs(trackpointEntities));
                trackViewModel.getTrackpointsList().removeObserver(this);
            }
        }
    };

    private Observer<TrackpointEntity> actualTrackpointObserver = new Observer<TrackpointEntity>() {
        @Override
        public void onChanged(@Nullable TrackpointEntity trackpointEntity) {
            //  // MyLog.d(TAG, "actualTrackpointObserver - getActualTrackpoint");
            if (trackpointEntity != null) {
                //  // MyLog.d(TAG, "actualTrackpointObserver - getActualTrackpoint - id: " + trackpointEntity.getTrackpointId());
                continueTrackOnMap(new LatLng(trackpointEntity.getLatitude(), trackpointEntity.getLongitude()));
            }
        }
    };

    private PolylineOptions polylineOptions;
    private Polyline polyline;

    private void drawTrackOnMap(List<LatLng> pointsCoordinates) {
        //  // MyLog.d(TAG, "drawTrackOnMap");
        setupPolyLine();
        if (googleMap != null) {
            polyline.setPoints(pointsCoordinates);
        }
    }

    private void setupPolyLine() {
        //  // MyLog.d(TAG, "setupPolyLine");
        polylineOptions = new PolylineOptions()
                .geodesic(true)
                .color(ContextCompat.getColor(this, R.color.colorPrimary))
                .width(getResources().getInteger(R.integer.polyline_width))
                .zIndex(30)
                .visible(true);

        if (googleMap != null) {
            polyline = googleMap.addPolyline(polylineOptions);
        }
    }

    private void clearTrackOnMap() {
        //  // MyLog.d(TAG, "clearTrackOnMap");
        if (polyline != null) {
            //  // MyLog.d(TAG, "clearTrackOnMap - removing polyline");
            polyline.remove();
        }
    }

    private void continueTrackOnMap(LatLng currentLatLng) {
        //  // MyLog.d(TAG, "continueTrackOnMap");
        if (polylineOptions == null) {
            setupPolyLine();
        }
        List<LatLng> points = polyline.getPoints();
        points.add(currentLatLng);
        polyline.setPoints(points);
    }

    private void centerTrackOnMap() {
        //  // MyLog.d(TAG, "centerTrackOnMap");
        trackViewModel.getTrack().observe(this, new Observer<TrackEntity>() {
            @Override
            public void onChanged(@Nullable TrackEntity track) {
                if (track != null) {
                    if (track.getNorthestPoint() != 0 || track.getSouthestPoint() != 0
                            || track.getWesternPoint() != 0 || track.getEasternPoint() != 0) {
                        LatLngBounds bounds = new LatLngBounds(
                                new LatLng(track.getSouthestPoint(), track.getWesternPoint()),
                                new LatLng(track.getNorthestPoint(), track.getEasternPoint()));
                        if (googleMap != null) {
                            updateCamera(bounds);
                        }
                    }
                }
            }
        });
    }

    private static List<LatLng> transformTrackpointsToLatLngs(List<TrackpointEntity> trackpoints) {
        List<LatLng> latLngs = new ArrayList<>(trackpoints.size());
        for (TrackpointEntity trackpoint : trackpoints) {
            latLngs.add(new LatLng(trackpoint.getLatitude(), trackpoint.getLongitude()));
        }
        return latLngs;
    }
}
