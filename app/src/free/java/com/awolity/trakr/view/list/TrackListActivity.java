package com.awolity.trakr.view.list;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.awolity.trakr.BuildConfig;
import com.awolity.trakr.R;
import com.awolity.trakr.sync.SyncService;
import com.awolity.trakr.view.detail.TrackDetailActivity;
import com.awolity.trakrviews.PrimaryPropertyViewIcon;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class TrackListActivity extends AppCompatActivity implements TrackListAdapter.TrackItemCallback {

    public static Intent getStarterIntent(Context context) {
        return new Intent(context, TrackListActivity.class);
    }

    private TrackListAdapter trackListAdapter;
    private TextView placeholderTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // MyLog.d(TAG, "onCreate");
        setContentView(R.layout.activity_track_list);
        placeholderTv = findViewById(R.id.tv_placeholder);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_track_list));

        // start syncing
        startService(new Intent(this, SyncService.class));

        setupAdview();
        setupRecyclerView();
        setupViewModel();
    }

    private void setupAdview() {
        AdView adview = findViewById(R.id.adView);
        if (BuildConfig.DEBUG) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("B20AC3BE6C392F942D699800329EDCBD")
                    .build();
            adview.loadAd(adRequest);
        } else {
            AdRequest adRequest = new AdRequest.Builder().build();
            adview.loadAd(adRequest);
        }
    }

    private void setupRecyclerView() {
        // MyLog.d(TAG, "setupRecyclerView");
        RecyclerView trackListRv = findViewById(R.id.rv_track_list);
        LinearLayoutManager trackListLayoutManager = new LinearLayoutManager(this);
        trackListLayoutManager.setOrientation(RecyclerView.VERTICAL);
        trackListRv.setLayoutManager(trackListLayoutManager);
        trackListAdapter = new TrackListAdapter(this, getLayoutInflater(), this);
        trackListRv.setAdapter(trackListAdapter);
        trackListRv.setHasFixedSize(true);
    }

    private void setupViewModel() {
        TrackListViewModel trackListViewModel = ViewModelProviders.of(this)
                .get(TrackListViewModel.class);
        trackListAdapter.setUnit(trackListViewModel.getUnit());
        trackListViewModel.getTrackDataListWithMapPoints().observe(this,
                trackDataWithMapPoints -> {
                    if (trackDataWithMapPoints != null) {
                        trackListAdapter.updateItems(trackDataWithMapPoints);
                        if (trackDataWithMapPoints.size() > 0) {
                            placeholderTv.setVisibility(View.INVISIBLE);
                        } else {
                            placeholderTv.setVisibility(View.VISIBLE);
                        }
                    } else {
                        placeholderTv.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onTrackItemClicked(long trackId, View itemView) {
        // MyLog.d(TAG, "onTrackItemClicked");
        PrimaryPropertyViewIcon spvDistance = itemView.findViewById(R.id.ppv_distance);
        PrimaryPropertyViewIcon spvDuration = itemView.findViewById(R.id.ppv_duration);
        PrimaryPropertyViewIcon spvAscent = itemView.findViewById(R.id.ppv_ascent);
        TextView titleTv = itemView.findViewById(R.id.tv_title);
        ImageView iconIv = itemView.findViewById(R.id.iv_icon);
        TextView dateTv = itemView.findViewById(R.id.tv_date);

        Intent intent = TrackDetailActivity.getStarterIntent(this, trackId);

        Pair<View, String> p1 = Pair.create(spvDistance,
                getString(R.string.transition_ppvi_distance));
        Pair<View, String> p2 = Pair.create(spvDuration,
                getString(R.string.transition_ppvi_duration));
        Pair<View, String> p3 = Pair.create(spvAscent,
                getString(R.string.transition_ppvi_ascent));
        Pair<View, String> p4 = Pair.create(titleTv,
                getString(R.string.transition_tv_title));
        Pair<View, String> p5 = Pair.create(iconIv,
                getString(R.string.transition_iv_icon));
        Pair<View, String> p6 = Pair.create(dateTv,
                getString(R.string.transition_tv_date));
        @SuppressWarnings("unchecked")
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, p1, p2, p3, p4, p5, p6);
        startActivity(intent, options.toBundle());
    }
}
