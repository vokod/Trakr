package com.awolity.trakr.view.detail;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.transition.Fade;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.awolity.trakr.BuildConfig;
import com.awolity.trakr.R;

public class TrackDetailActivity extends AppCompatActivity
        implements EditTitleDialog.EditTitleDialogListener {

    private static final String TAG_MAP_FRAGMENT = "tag_map_fragment";
    private static final String TAG_DATA_FRAGMENT = "tag_data_fragment";
    private static final String TAG_CHARTS_FRAGMENT = "tag_charts_fragment";
    private static final String KEY_SELECTED_FRAGMENT = "key_selected_fragment";
    private static final String EXTRA_TRACK_ID = "extra_track_id";
    private static final int PERMISSION_REQUEST_CODE = 2;
    private BottomNavigationView bottomNavigationView;
    private TrackDetailViewModel trackDetailViewModel;
    private long trackId;

    public static Intent getStarterIntent(Context context, long trackId) {
        Intent intent = new Intent(context, TrackDetailActivity.class);
        intent.putExtra(EXTRA_TRACK_ID, trackId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail);
        trackId = getIntent().getLongExtra(EXTRA_TRACK_ID, 0);
        postponeEnterTransition();

        setupViewModel();
        setupWidgets();
        setupBottomSheetNavigation();
        if (savedInstanceState == null) {
            showDataFragment();
        } else {
            switch (savedInstanceState.getInt(KEY_SELECTED_FRAGMENT)) {
                case R.id.action_map:
                    showMapFragment();
                    break;
                case R.id.action_data:
                    showDataFragment();
                    break;
                case R.id.action_charts:
                    showChartsFragment();
                    break;
                default:
                    throw new IllegalStateException("Unknown selected menu item: "
                            + savedInstanceState.getInt(KEY_SELECTED_FRAGMENT));
            }
        }
    }

    private void setupWidgets() {
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);
        bottomNavigationView = findViewById(R.id.navigation);
    }

    private void setupViewModel(){
        trackDetailViewModel = ViewModelProviders.of(this).get(TrackDetailViewModel.class);
        trackDetailViewModel.init(trackId);
    }

    private void setupBottomSheetNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.action_data);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_map:
                        // MyLog.d(TAG, "onNavigationItemSelected - action_map");
                        showMapFragment();
                        return true;
                    case R.id.action_data:
                        showDataFragment();
                        return true;
                    case R.id.action_charts:
                        showChartsFragment();
                        return true;
                }
                return false;
            }
        });
    }

    private void showMapFragment() {
        TrackDetailActivityMapFragment mapFragment
                = (TrackDetailActivityMapFragment) getSupportFragmentManager().findFragmentByTag(TAG_MAP_FRAGMENT);
        if (mapFragment == null) {
            mapFragment = TrackDetailActivityMapFragment.newInstance();
            mapFragment.setEnterTransition(new Fade());
            mapFragment.setExitTransition(new Fade());
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, mapFragment, TAG_MAP_FRAGMENT)
                .commit();
    }

    private void showDataFragment() {
        TrackDetailActivityDataFragment dataFragment
                = (TrackDetailActivityDataFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATA_FRAGMENT);
        if (dataFragment == null) {
            dataFragment = TrackDetailActivityDataFragment.newInstance();
            dataFragment.setEnterTransition(new Fade());
            dataFragment.setExitTransition(new Fade());
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, dataFragment, TAG_DATA_FRAGMENT)
                .commit();
    }

    private void showChartsFragment() {
        TrackDetailActivityChartsFragment chartsFragment
                = (TrackDetailActivityChartsFragment) getSupportFragmentManager().findFragmentByTag(TAG_CHARTS_FRAGMENT);
        if (chartsFragment == null) {
            chartsFragment = TrackDetailActivityChartsFragment.newInstance();
            chartsFragment.setEnterTransition(new Fade());
            chartsFragment.setExitTransition(new Fade());
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, chartsFragment, TAG_CHARTS_FRAGMENT)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    trackDetailViewModel.exportTrack();
                } else {
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
            trackDetailViewModel.deleteTrack();
            finish();
            return true;
        } else if (id == R.id.action_export) {
            if (TrackDetailActivityUtils.checkPermission(this, PERMISSION_REQUEST_CODE)) {
                trackDetailViewModel.exportTrack();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTitleEdited(String title) {
        trackDetailViewModel.updateTrackTitle(title);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_FRAGMENT, bottomNavigationView.getSelectedItemId());
    }
}
