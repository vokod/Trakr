package com.awolity.trakr.view.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.awolity.trakr.R;
import com.awolity.trakr.databinding.ActivityTrackDetailBinding;
import com.awolity.trakr.viewmodel.TrackViewModel;

public class TrackDetailActivity extends AppCompatActivity {

    private static final String EXTRA_TRACK_ID = "extra_track_id";

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
            // TODO: export track
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
