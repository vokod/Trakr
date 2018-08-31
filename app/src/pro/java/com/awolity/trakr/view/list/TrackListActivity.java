package com.awolity.trakr.view.list;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.sync.SyncService;
import com.awolity.trakr.view.detail.TrackDetailActivity;
import com.awolity.trakr.view.main.TrackRecorderServiceManager;
import com.awolity.trakr.viewmodel.SettingsViewModel;
import com.awolity.trakr.viewmodel.TrackListViewModel;
import com.awolity.trakrutils.Constants;
import com.awolity.trakrutils.Utility;
import com.awolity.trakrviews.PrimaryPropertyViewIcon;
import com.crashlytics.android.Crashlytics;

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

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_track_list));

        // start syncing
        startService(new Intent(this, SyncService.class));

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
                Constants.SIMPLIFIED_TRACK_POINT_MAX_NUMBER_FOR_LIST_ITEMS)
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
    public void onTrackItemClicked(long trackId, View itemView) {
        // MyLog.d(TAG, "onTrackItemClicked");
        PrimaryPropertyViewIcon spvDistance = itemView.findViewById(R.id.spv_distance);
        PrimaryPropertyViewIcon spvDuration = itemView.findViewById(R.id.spv_duration);
        PrimaryPropertyViewIcon spvAscent = itemView.findViewById(R.id.spv_ascent);
        TextView titleTv = itemView.findViewById(R.id.tv_title);
        ImageView iconIv = itemView.findViewById(R.id.iv_icon);
        TextView dateTv = itemView.findViewById(R.id.tv_date);

        Intent intent = TrackDetailActivity.getStarterIntent(this, trackId,
                Utility.convertToBitmap(iconIv.getDrawable(),
                        iconIv.getLayoutParams().width, iconIv.getLayoutParams().height));

        Pair<View, String> p1 = Pair.create((View) spvDistance,
                getString(R.string.transition_ppvi_distance));
        Pair<View, String> p2 = Pair.create((View) spvDuration,
                getString(R.string.transition_ppvi_duration));
        Pair<View, String> p3 = Pair.create((View) spvAscent,
                getString(R.string.transition_ppvi_ascent));
        Pair<View, String> p4 = Pair.create((View) titleTv,
                getString(R.string.transition_tv_title));
        Pair<View, String> p5 = Pair.create((View) iconIv,
                getString(R.string.transition_iv_icon));
        Pair<View, String> p6 = Pair.create((View) dateTv,
                getString(R.string.transition_tv_date));
        @SuppressWarnings("unchecked")
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, p1, p2, p3, p4, p5, p6);
        startActivity(intent, options.toBundle());
    }
}
