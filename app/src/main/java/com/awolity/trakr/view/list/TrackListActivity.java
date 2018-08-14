package com.awolity.trakr.view.list;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.utils.Constants;
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.view.detail.TrackDetailActivity;
import com.awolity.trakr.viewmodel.TrackListViewModel;

import java.util.List;

import static android.widget.LinearLayout.VERTICAL;

public class TrackListActivity extends AppCompatActivity implements TrackListAdapter.TrackItemCallback {

    public static Intent getStarterIntent(Context context) {
        return new Intent(context, TrackListActivity.class);
    }

    private static final String TAG = TrackListActivity.class.getSimpleName();
    private TrackListAdapter trackListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      // MyLog.d(TAG, "onCreate");
        setContentView(R.layout.activity_track_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_track_list));
        setupRecyclerView();
        setupViewModel();
    }

    private void setupRecyclerView() {
      // MyLog.d(TAG, "setupRecyclerView");
        RecyclerView trackListRv = findViewById(R.id.rv_track_list);
        LinearLayoutManager trackListLayoutManager = new LinearLayoutManager(this);
        trackListLayoutManager.setOrientation(VERTICAL);
        trackListRv.setLayoutManager(trackListLayoutManager);
        //noinspection ConstantConditions
        trackListAdapter = new TrackListAdapter(this, getLayoutInflater(), this);
        trackListRv.setAdapter(trackListAdapter);
        trackListRv.setHasFixedSize(true);
    }

    private void setupViewModel() {
      // MyLog.d(TAG, "setupViewModel");
        TrackListViewModel trackListViewModel = ViewModelProviders.of(this)
                .get(TrackListViewModel.class);
        trackListViewModel.getSimplifiedTracksWithPoints(
                Constants.SIMPLIFIED_TRACK_POINT_MAX_NUMBER_FOR_LIST_ITEM_POLYLINES)
                .observe(this, new Observer<List<TrackWithPoints>>() {
            @Override
            public void onChanged(@Nullable List<TrackWithPoints> trackWithPointsList) {
                if (trackWithPointsList != null) {
                    trackListAdapter.updateItems(trackWithPointsList);
                }
            }
        });
    }

    @Override
    public void onTrackItemClicked(long trackId) {
      // MyLog.d(TAG, "onTrackItemClicked");
        Intent intent = TrackDetailActivity.getStarterIntent(this, trackId);
        startActivity(intent);
    }
}
