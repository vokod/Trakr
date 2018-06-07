package com.awolity.trakr.view.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.awolity.trakr.R;
import com.awolity.trakr.customviews.PrimaryPropertyView;
import com.awolity.trakr.customviews.SecondaryPropertyView;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.utils.StringUtils;
import com.awolity.trakr.viewmodel.TrackViewModel;

import java.util.Locale;

public class BottomSheetTrackFragment extends BottomSheetBaseFragment {

    private static final String KEY_DISTANCEVIEW_VALUE = "key_distanceView_value";
    private static final String KEY_ASCENTVIEW_VALUE = "key_ascentView_value";
    private static final String KEY_DESCENTVIEW_VALUE = "key_descentView_value";
    private static final String KEY_ELAPSEDVIEW_VALUE = "key_elapsedTimeView_value";
    private static final String KEY_MINALTITUDECEVIEW_VALUE = "key_minAltitudeView_value";
    private static final String KEY_MAXALTITUDEVIEW_VALUE = "key_maxAltitudeView_value";
    private static final String KEY_AVGSPEEDVIEW_VALUE = "key_avgSpeedView_value";
    private static final String KEY_MAXSPEEDVIEW_VALUE = "key_maxSpeedView_value";

    private Handler handler;
    private Runnable elapsedTimeUpdater;
    private long startTime;
    private TrackViewModel trackViewModel;
    private boolean isRecording;
    private PrimaryPropertyView distanceView, ascentView, descentView;
    private SecondaryPropertyView elapsedTimeView, minAltitudeView, maxAltitudeView, avgSpeedView,
            maxSpeedView;
    private TextView tvPlaceholder;
    private long trackId = -1;

    public static BottomSheetTrackFragment newInstance(@SuppressWarnings("SameParameterValue") String title) {
        BottomSheetTrackFragment fragment = new BottomSheetTrackFragment();
        fragment.setTitle(title);
        return fragment;
    }

    private static final String LOG_TAG = BottomSheetTrackFragment.class.getSimpleName();

    public BottomSheetTrackFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.d(LOG_TAG, "onCreate");
        //noinspection ConstantConditions
        trackViewModel = ViewModelProviders.of(getActivity()).get(TrackViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.d(LOG_TAG, "onCreateView");
        View view = inflater.inflate(
                R.layout.activity_main_fragment_bottom_sheet_track, container, false);

        setupWidgets(view);
        setupElapsedTimeUpdater();

        setDataVisibility(false);

        if (isRecording && trackId != -1) {
            startTrackDataUpdate(trackId);
        }

        return view;
    }

    private void setupWidgets(View view) {
        MyLog.d(LOG_TAG, "setupWidgets");
        distanceView = view.findViewById(R.id.distanceView);
        ascentView = view.findViewById(R.id.ascentView);
        descentView = view.findViewById(R.id.descentView);
        elapsedTimeView = view.findViewById(R.id.elapsedTimeView);
        minAltitudeView = view.findViewById(R.id.minAltitudeView);
        maxAltitudeView = view.findViewById(R.id.maxAltitudeView);
        avgSpeedView = view.findViewById(R.id.avgSpeedView);
        maxSpeedView = view.findViewById(R.id.maxSpeedView);
        tvPlaceholder = view.findViewById(R.id.tvPlaceholder);
    }

    private void setupElapsedTimeUpdater() {
        handler = new Handler();
        elapsedTimeUpdater = new Runnable() {
            @Override
            public void run() {
                updateElapsedTime();
                handler.postDelayed(elapsedTimeUpdater, 1000);
            }
        };
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        MyLog.d(LOG_TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            distanceView.setValue(savedInstanceState.getString(KEY_DISTANCEVIEW_VALUE));
            ascentView.setValue(savedInstanceState.getString(KEY_ASCENTVIEW_VALUE));
            descentView.setValue(savedInstanceState.getString(KEY_DESCENTVIEW_VALUE));
            elapsedTimeView.setValue(savedInstanceState.getString(KEY_ELAPSEDVIEW_VALUE));
            minAltitudeView.setValue(savedInstanceState.getString(KEY_MINALTITUDECEVIEW_VALUE));
            maxAltitudeView.setValue(savedInstanceState.getString(KEY_MAXALTITUDEVIEW_VALUE));
            avgSpeedView.setValue(savedInstanceState.getString(KEY_AVGSPEEDVIEW_VALUE));
            maxSpeedView.setValue(savedInstanceState.getString(KEY_MAXSPEEDVIEW_VALUE));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isRecording) {
            continueElapsedTimer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {
            stopElapsedTimer();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        MyLog.d(LOG_TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putString(KEY_DISTANCEVIEW_VALUE, distanceView.getValue());
        outState.putString(KEY_ASCENTVIEW_VALUE, ascentView.getValue());
        outState.putString(KEY_DESCENTVIEW_VALUE, descentView.getValue());
        outState.putString(KEY_ELAPSEDVIEW_VALUE, elapsedTimeView.getValue());
        outState.putString(KEY_MINALTITUDECEVIEW_VALUE, minAltitudeView.getValue());
        outState.putString(KEY_MAXALTITUDEVIEW_VALUE, maxAltitudeView.getValue());
        outState.putString(KEY_AVGSPEEDVIEW_VALUE, avgSpeedView.getValue());
        outState.putString(KEY_MAXSPEEDVIEW_VALUE, maxAltitudeView.getValue());
    }

    public void startTrackDataUpdate(long trackId) {
        MyLog.d(LOG_TAG, "startTrackDataUpdate");
        this.trackId = trackId;
        if (checkViews()) {
            setDataVisibility(true);
            startObserve(trackId);
            startElapsedTimer();
        }
        isRecording = true;
    }

    public void stopTrackDataUpdate() {
        MyLog.d(LOG_TAG, "stopTrackDataUpdate");
        setDataVisibility(false);
        stopObserve();
        stopElapsedTimer();
    }

    private void startElapsedTimer() {
        setStartTime(System.currentTimeMillis());
        continueElapsedTimer();
    }

    private void stopElapsedTimer() {
        handler.removeCallbacks(elapsedTimeUpdater);
    }

    private void continueElapsedTimer() {
        elapsedTimeUpdater.run();
    }

    private void startObserve(long trackId) {
        MyLog.d(LOG_TAG, "startObserve");
        trackViewModel.init(trackId);
        trackViewModel.getTrack().observe(getActivity(), trackEntityObserver);
    }

    private void stopObserve(){
        trackViewModel.getTrack().removeObserver(trackEntityObserver);
    }

    private Observer<TrackEntity> trackEntityObserver = new Observer<TrackEntity>() {
        @Override
        public void onChanged(@Nullable TrackEntity trackEntity) {
            // MyLog.d(LOG_TAG, "trackEntityObserver.onChanged");
            if (trackEntity != null) {
                // MyLog.d(LOG_TAG, "trackEntityObserver.onChanged - track NOT null");
                setData(trackEntity);
            }
        }
    };

    private void setData(TrackEntity track) {
        // MyLog.d(LOG_TAG, "setData");
        setDistance(track.getDistance());
        setAscent(track.getAscent());
        setDescent(track.getDescent());
        setMinAltitude(track.getMaxAltitude());
        setMaxAltitude(track.getMinAltitude());
        setMaxSpeed(track.getMaxSpeed());
        setAvgSpeed(track.getAvgSpeed());
        setStartTime(track.getStartTime());
    }

    private void updateElapsedTime() {
        //MyLog.d(LOG_TAG, "updateElapsedTime");
        if (startTime != 0) {
            setElapsedTime(System.currentTimeMillis() - startTime);
        }
    }

    private void setDataVisibility(boolean isRecording) {
        MyLog.d(LOG_TAG, "setDataVisibility");
        if (!checkViews()) {
            return;
        }

        // TODO: ezt valami animációval
        if (isRecording) {
            resetData();
            tvPlaceholder.setVisibility(View.INVISIBLE);
            distanceView.setVisibility(View.VISIBLE);
            ascentView.setVisibility(View.VISIBLE);
            descentView.setVisibility(View.VISIBLE);
            elapsedTimeView.setVisibility(View.VISIBLE);
            minAltitudeView.setVisibility(View.VISIBLE);
            maxAltitudeView.setVisibility(View.VISIBLE);
            avgSpeedView.setVisibility(View.VISIBLE);
            maxSpeedView.setVisibility(View.VISIBLE);
        } else {
            tvPlaceholder.setVisibility(View.VISIBLE);
            distanceView.setVisibility(View.INVISIBLE);
            ascentView.setVisibility(View.INVISIBLE);
            descentView.setVisibility(View.INVISIBLE);
            elapsedTimeView.setVisibility(View.INVISIBLE);
            minAltitudeView.setVisibility(View.INVISIBLE);
            maxAltitudeView.setVisibility(View.INVISIBLE);
            avgSpeedView.setVisibility(View.INVISIBLE);
            maxSpeedView.setVisibility(View.INVISIBLE);
        }
    }

    private void resetData() {
        // MyLog.d(LOG_TAG, "resetData");

        if (!checkViews()) {
            return;
        }
        startTime = 0;

        distanceView.setValue(getActivity().getString(R.string.distance_view_value));
        distanceView.setUnit(getActivity().getString(R.string.distance_view_unit));
        distanceView.setLabel(getActivity().getString(R.string.distance_view_label));

        ascentView.setValue(getActivity().getString(R.string.ascent_view_value));
        ascentView.setUnit(getActivity().getString(R.string.ascent_view_unit));
        ascentView.setLabel(getActivity().getString(R.string.ascent_view_label));

        descentView.setValue(getActivity().getString(R.string.descent_view_value));
        descentView.setUnit(getActivity().getString(R.string.descent_view_unit));
        descentView.setLabel(getActivity().getString(R.string.descent_view_label));

        elapsedTimeView.setValue(getActivity().getString(R.string.elapsed_time_view_value));
        elapsedTimeView.setUnit(getActivity().getString(R.string.elapsed_time_view_unit));
        elapsedTimeView.setLabel(getActivity().getString(R.string.elapsed_time_view_label));

        minAltitudeView.setValue(getActivity().getString(R.string.min_altitude_view_value));
        minAltitudeView.setUnit(getActivity().getString(R.string.min_altitude_view_unit));
        minAltitudeView.setLabel(getActivity().getString(R.string.min_altitude_view_label));

        maxAltitudeView.setValue(getActivity().getString(R.string.max_altitude_view_value));
        maxAltitudeView.setUnit(getActivity().getString(R.string.max_altitude_view_unit));
        maxAltitudeView.setLabel(getActivity().getString(R.string.max_altitude_view_label));

        avgSpeedView.setValue(getActivity().getString(R.string.avg_speed_view_value));
        avgSpeedView.setUnit(getActivity().getString(R.string.avg_speed_view_unit));
        avgSpeedView.setLabel(getActivity().getString(R.string.avg_speed_view_label));

        maxSpeedView.setValue(getActivity().getString(R.string.max_speed_view_value));
        maxSpeedView.setUnit(getActivity().getString(R.string.max_speed_view_unit));
        maxSpeedView.setLabel(getActivity().getString(R.string.max_speed_view_label));
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    private void setDistance(double distance) {
        distanceView.setValue(StringUtils.getDistanceAsThreeCharactersString(distance));
    }

    private void setAscent(double ascent) {
        ascentView.setValue(String.format(Locale.getDefault(), "%.0f", ascent));
    }

    private void setDescent(double descent) {
        descentView.setValue(String.format(Locale.getDefault(), "%.0f", descent));
    }

    private void setElapsedTime(long elapsedTime) {
        elapsedTimeView.setValue(StringUtils.getElapsedTimeAsString(elapsedTime));
    }

    private void setMinAltitude(double minAltitude) {
        String minAltitudeString = String.format(Locale.getDefault(), "%.0f", minAltitude);
        minAltitudeView.setValue(minAltitudeString);
    }

    private void setMaxAltitude(double maxAltitude) {
        String maxAltitudeString = String.format(Locale.getDefault(), "%.0f", maxAltitude);
        maxAltitudeView.setValue(maxAltitudeString);
    }

    private void setMaxSpeed(double maxSpeed) {
        maxSpeedView.setValue(StringUtils.getSpeedAsThreeCharactersString(maxSpeed));
    }

    private void setAvgSpeed(double avgSpeed) {
        avgSpeedView.setValue(StringUtils.getSpeedAsThreeCharactersString(avgSpeed));
    }

    private boolean checkViews() {
        return distanceView != null;
    }
}
