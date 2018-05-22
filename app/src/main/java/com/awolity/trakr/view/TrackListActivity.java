package com.awolity.trakr.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toolbar;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.viewmodel.TrackListViewModel;

import java.util.List;

import static android.widget.LinearLayout.VERTICAL;

public class TrackListActivity extends AppCompatActivity {

    public static Intent getStarterIntent(Context context) {
        return new Intent(context, TrackListActivity.class);
    }

    private TrackListAdapter trackListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        setupRecyclerView();
        setupViewModel();
    }

    private void setupToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getString(R.string.title_activity_track_list));
    }

    private void setupRecyclerView() {
        RecyclerView trackListRv = findViewById(R.id.rv_track_list);
        LinearLayoutManager trackListLayoutManager = new LinearLayoutManager(this);
        trackListLayoutManager.setOrientation(VERTICAL);
        trackListRv.setLayoutManager(trackListLayoutManager);
        //noinspection ConstantConditions
        trackListAdapter = new TrackListAdapter(getLayoutInflater());
        trackListRv.setAdapter(trackListAdapter);

        @SuppressWarnings("ConstantConditions")
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(this, trackListLayoutManager.getOrientation());
        trackListRv.addItemDecoration(dividerItemDecoration);
    }

    private void setupViewModel() {
        TrackListViewModel trackListViewModel = ViewModelProviders.of(this).get(TrackListViewModel.class);
        trackListViewModel.getTracks().observe(this, new Observer<List<TrackEntity>>() {
            @Override
            public void onChanged(@Nullable List<TrackEntity> trackEntities) {
                if (trackEntities != null) {
                    trackListAdapter.updateItems(trackEntities);
                }
            }
        });
    }
}
