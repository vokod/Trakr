package com.awolity.trakr.view.detail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awolity.trakr.R;
import com.awolity.trakr.customviews.PrimaryPropertyViewIcon;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.utils.StringUtils;
import com.awolity.trakr.viewmodel.TrackViewModel;

import java.util.Locale;


public class TrackDetailActivityDataFragment extends Fragment {


    private static final String ARG_TRACK_ID = "track_id";
    private static final String LOG_TAG = TrackDetailActivityDataFragment.class.getSimpleName();

    private long trackId;
    private TrackViewModel trackViewModel;
    private PrimaryPropertyViewIcon durationPpvi, distancePpvi, ascentPpvi, descentPpvi, maxSpeedPpvi, avgSpeedPpvi, maxAltitudePpvi, minAltitudePpvi;

    public static TrackDetailActivityDataFragment newInstance(long trackId) {
        TrackDetailActivityDataFragment fragment = new TrackDetailActivityDataFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TRACK_ID, trackId);
        fragment.setArguments(args);
        return fragment;
    }

    public TrackDetailActivityDataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.d(LOG_TAG, "onCreate - " + this.hashCode());
        if (getArguments() != null) {
            trackId = getArguments().getLong(ARG_TRACK_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_track_detail_fragment_data, container, false);
        setupWidgets(view);
        resetWidgets();
        setupViewModel();
        return view;
    }

    private void setupWidgets(View view) {
        durationPpvi = view.findViewById(R.id.ppvi_duration);
        distancePpvi = view.findViewById(R.id.ppvi_distance);
        ascentPpvi = view.findViewById(R.id.ppvi_ascent);
        descentPpvi = view.findViewById(R.id.ppvi_descent);
        maxSpeedPpvi = view.findViewById(R.id.ppvi_max_speed);
        avgSpeedPpvi = view.findViewById(R.id.ppvi_avg_speed);
        maxAltitudePpvi = view.findViewById(R.id.ppvi_max_altitude);
        minAltitudePpvi = view.findViewById(R.id.ppvi_min_altitude);
    }

    private void resetWidgets() {
        // TODO: extract
        durationPpvi.setup("Duration", "s", "00:00", R.drawable.ic_duration);
        distancePpvi.setup("Distance", "km", "0", R.drawable.ic_distance);
        ascentPpvi.setup("Ascent", "m", "0", R.drawable.ic_ascent);
        descentPpvi.setup("Descent", "m", "0", R.drawable.ic_descent);
        maxSpeedPpvi.setup("Max.Speed", "km/h", "0", R.drawable.ic_max_speed);
        avgSpeedPpvi.setup("Avg.Speed", "km/h", "-", R.drawable.ic_avg_speed);
        maxAltitudePpvi.setup("Max.Altitude", "m", "-", R.drawable.ic_max_altitude);
        minAltitudePpvi.setup("Min.Altitude", "m", "-", R.drawable.ic_min_altitude);
        // TODO: további ötletek: max pace, avg pace, avg slope, max slope, startTime, endTime
        // TODO. plusz a layoutot scrollerbe,
    }

    private void setupViewModel() {
        trackViewModel = ViewModelProviders.of(getActivity()).get(TrackViewModel.class);
        trackViewModel.getTrackWithPoints().observe(this, new Observer<TrackWithPoints>() {
            @Override
            public void onChanged(@Nullable TrackWithPoints trackWithPoints) {
                if (trackWithPoints != null) {
                    setData(trackWithPoints);
                }
            }
        });
    }

    private void setData(TrackWithPoints trackWithPoints) {
        MyLog.d(LOG_TAG, "setData");
        durationPpvi.setValue(StringUtils.getElapsedTimeAsString(trackWithPoints.getElapsedTime()));
        distancePpvi.setValue(StringUtils.getDistanceAsThreeCharactersString(trackWithPoints.getDistance()));
        ascentPpvi.setValue(String.format(Locale.getDefault(), "%.0f", trackWithPoints.getAscent()));
        descentPpvi.setValue(String.format(Locale.getDefault(), "%.0f", trackWithPoints.getDescent()));
        maxSpeedPpvi.setValue(StringUtils.getSpeedAsThreeCharactersString(trackWithPoints.getMaxSpeed()));
        avgSpeedPpvi.setValue(StringUtils.getSpeedAsThreeCharactersString(trackWithPoints.getAvgSpeed()));
        minAltitudePpvi.setValue(String.format(Locale.getDefault(), "%.0f", trackWithPoints.getMinAltitude()));
        maxAltitudePpvi.setValue(String.format(Locale.getDefault(), "%.0f", trackWithPoints.getMaxAltitude()));
    }
}
