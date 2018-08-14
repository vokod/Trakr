package com.awolity.trakr.view.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.location.LocationManager;
import com.awolity.trakr.utils.Constants;
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.utils.PreferenceUtils;
import com.awolity.trakr.utils.Utility;
import com.awolity.trakr.view.detail.TrackDetailActivity;
import com.awolity.trakr.view.list.TrackListActivity;
import com.awolity.trakr.view.main.bottom.BottomSheetBaseFragment;
import com.awolity.trakr.view.main.bottom.BottomSheetChartsFragment;
import com.awolity.trakr.view.main.bottom.BottomSheetFragmentPagerAdapter;
import com.awolity.trakr.view.main.bottom.BottomSheetPointFragment;
import com.awolity.trakr.view.main.bottom.BottomSheetTrackFragment;
import com.awolity.trakr.viewmodel.AppUserViewModel;
import com.awolity.trakr.viewmodel.LocationViewModel;
import com.awolity.trakr.viewmodel.TrackViewModel;
import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        TrackRecorderServiceManager.TrackRecorderServiceManagerListener {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final float ZOOM_LEVEL_INITIAL = 15;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 22;

    private GoogleMap googleMap;
    private BottomSheetPointFragment pointFragment;
    private BottomSheetTrackFragment trackFragment;
    private BottomSheetChartsFragment chartsFragment;
    private FloatingActionButton fab;

    private LocationViewModel locationViewModel;
    private TrackViewModel trackViewModel;
    private AppUserViewModel appUserViewModel;

    private TrackRecorderServiceManager serviceManager;
    private MainActivityStatus status;
    private long trackId = Constants.NO_LAST_RECORDED_TRACK;

    private Menu menu;
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
        setupMapFragment();
        setupLocationViewModel();
        setupAppUserViewModel();
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
            pointFragment.setTitle(getString(R.string.bottom_sheet_label_point));

            trackFragment = (BottomSheetTrackFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, BottomSheetTrackFragment.class.getName());
            trackFragment.setTitle(getString(R.string.bottom_sheet_label_track));

            chartsFragment = (BottomSheetChartsFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, BottomSheetChartsFragment.class.getName());
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

    private void setupAppUserViewModel() {
        appUserViewModel = ViewModelProviders.of(this).get(AppUserViewModel.class);
    }

    private void setupTrackRecorderService() {
        MyLog.d(TAG, "setupTrackRecorderService");
        serviceManager = new TrackRecorderServiceManager(this);
        if (TrackRecorderServiceManager.isServiceRunning(this)) {
            MyLog.d(TAG, "setupTrackRecorderService - service is running");
            status.setContinueRecording();
            long trackId = PreferenceUtils.getLastRecordedTrackId(this);
            if (trackId != Constants.NO_LAST_RECORDED_TRACK) {
                polylineManager = new PolylineManager(this);
                setupTrackViewModel(trackId);
                this.trackId = trackId;
                trackFragment.startTrackDataUpdate(trackId);
                chartsFragment.startTrackDataUpdate(trackId);
                status.setRecording(true);
                MainActivityUtils.startFabAnimation(fab);
            }
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
                updateCamera(CameraPosition.fromLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), ZOOM_LEVEL_INITIAL));
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

    public void onRecordFabClick(View view) {
        if (!MainActivityUtils.isLocationPermissionEnabled(this)) {
            MainActivityUtils.checkLocationPermission(this, PERMISSION_REQUEST_CODE);
            return;
        }
        locationViewModel.isLocationSettingsGood(new LocationManager.LocationSettingsCallback() {
            @Override
            public void onLocationSettingsDetermined(boolean isSettingsGood) {
                if (isSettingsGood) {
                    serviceManager.startStopFabClicked();
                } else {
                    showLocationSettingsDialog();
                }
            }
        });
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

    private final Observer<List<TrackpointEntity>> trackpointsListObserver = new Observer<List<TrackpointEntity>>() {
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

    private final Observer<TrackpointEntity> actualTrackpointObserver = new Observer<TrackpointEntity>() {
        @Override
        public void onChanged(@Nullable TrackpointEntity trackpointEntity) {
            if (trackpointEntity != null) {
                if (polylineManager != null) {
                    polylineManager.continuePolyline(googleMap,
                            new LatLng(trackpointEntity.getLatitude(), trackpointEntity.getLongitude()));
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
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
                } else {
                    // permission denied, boo!
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_main_menu, menu);
        this.menu = menu;

        MenuItem synchronisationItem = menu.findItem(R.id.action_synchronisation);
        if (appUserViewModel.IsAppUserLoggedIn()) {
            synchronisationItem.setTitle(getString(R.string.disable_cloud_sync));
        } else {
            synchronisationItem.setTitle(getString(R.string.enable_cloud_sync));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_list_tracks) {
            startActivity(TrackListActivity.getStarterIntent(this));
            return true;
        } else if (id == R.id.action_synchronisation) {
            if (appUserViewModel.IsAppUserLoggedIn()) {
                MainActivityUtils.showLogoutAlertDialog(this, appUserViewModel, item);
            } else {
                startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()))
                        .build(), RC_SIGN_IN);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                MenuItem synchronisationItem = menu.findItem(R.id.action_synchronisation);
                synchronisationItem.setTitle(getString(R.string.disable_cloud_sync));
                Toast.makeText(this, getString(R.string.you_are_logged_in), Toast.LENGTH_LONG).show();
                appUserViewModel.signIn();
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // UserEntity pressed back button
                    MainActivityUtils.showToast(this, getString(R.string.login_error_cancel));
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    MainActivityUtils.showToast(this, getString(R.string.login_error_no_internet));
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    MainActivityUtils.showToast(this, getString(R.string.login_error_unknown_error));
                    return;
                }
            }
            MainActivityUtils.showToast(this, getString(R.string.login_error_unknown_response));
        }
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
        trackViewModel.getTrackWithPoints().observe(this, new Observer<TrackWithPoints>() {
            @Override
            public void onChanged(@Nullable TrackWithPoints trackWithPoints) {
                if (!status.isRecording()) {
                    if (trackWithPoints != null) {
                        if (trackId != Constants.NO_LAST_RECORDED_TRACK) {
                            if (trackWithPoints.getTrackPoints().size() > 1) {
                                trackViewModel.finishRecording();
                                Intent intent = TrackDetailActivity.getStarterIntent(MainActivity.this, trackId);
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
}
