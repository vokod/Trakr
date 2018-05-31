package com.awolity.trakr.view.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.databinding.ActivityMainFragmentBottomSheetTrackBinding;
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.utils.StringUtils;
import com.awolity.trakr.viewmodel.TrackViewModel;

import java.util.Locale;

public class BottomSheetTrackFragment extends BottomSheetBaseFragment {

    private ActivityMainFragmentBottomSheetTrackBinding binding;

    private static final String KEY_DISTANCEVIEW_VALUE = "key_distanceView_value";
    private static final String KEY_ASCENTVIEW_VALUE = "key_ascentView_value";
    private static final String KEY_DESCENTVIEW_VALUE = "key_descentView_value";
    private static final String KEY_ELAPSEDVIEW_VALUE = "key_elapsedTimeView_value";
    private static final String KEY_MINALTITUDECEVIEW_VALUE = "key_minAltitudeView_value";
    private static final String KEY_MAXALTITUDEVIEW_VALUE = "key_maxAltitudeView_value";
    private static final String KEY_AVGSPEEDVIEW_VALUE = "key_avgSpeedView_value";
    private static final String KEY_MAXSPEEDVIEW_VALUE = "key_maxSpeedView_value";

    private Handler handler;
    private Runnable uiUpdater;
    private long startTime;
    private TrackViewModel trackViewModel;
    private boolean isRecording;

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
        // MyLog.d(LOG_TAG, "onCreate");
        //noinspection ConstantConditions
        trackViewModel = ViewModelProviders.of(getActivity()).get(TrackViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // MyLog.d(LOG_TAG, "onCreateView");

        binding = DataBindingUtil.inflate(
                inflater, R.layout.activity_main_fragment_bottom_sheet_track, container, false);

        handler = new Handler();
        uiUpdater = new Runnable() {
            @Override
            public void run() {
                updateUi();
                handler.postDelayed(uiUpdater, 1000);
            }
        };

        setDataVisibility(false);

        if(isRecording &&trackId != -1){
            start(trackId);
        }
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // MyLog.d(LOG_TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            binding.distanceView.setValue(savedInstanceState.getString(KEY_DISTANCEVIEW_VALUE));
            binding.ascentView.setValue(savedInstanceState.getString(KEY_ASCENTVIEW_VALUE));
            binding.descentView.setValue(savedInstanceState.getString(KEY_DESCENTVIEW_VALUE));
            binding.elapsedTimeView.setValue(savedInstanceState.getString(KEY_ELAPSEDVIEW_VALUE));
            binding.minAltitudeView.setValue(savedInstanceState.getString(KEY_MINALTITUDECEVIEW_VALUE));
            binding.maxAltitudeView.setValue(savedInstanceState.getString(KEY_MAXALTITUDEVIEW_VALUE));
            binding.avgSpeedView.setValue(savedInstanceState.getString(KEY_AVGSPEEDVIEW_VALUE));
            binding.maxAltitudeView.setValue(savedInstanceState.getString(KEY_MAXSPEEDVIEW_VALUE));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // MyLog.d(LOG_TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putString(KEY_DISTANCEVIEW_VALUE, binding.distanceView.getValue());
        outState.putString(KEY_ASCENTVIEW_VALUE, binding.ascentView.getValue());
        outState.putString(KEY_DESCENTVIEW_VALUE, binding.descentView.getValue());
        outState.putString(KEY_ELAPSEDVIEW_VALUE, binding.elapsedTimeView.getValue());
        outState.putString(KEY_MINALTITUDECEVIEW_VALUE, binding.minAltitudeView.getValue());
        outState.putString(KEY_MAXALTITUDEVIEW_VALUE, binding.maxAltitudeView.getValue());
        outState.putString(KEY_AVGSPEEDVIEW_VALUE, binding.avgSpeedView.getValue());
        outState.putString(KEY_MAXSPEEDVIEW_VALUE, binding.maxAltitudeView.getValue());
    }

    private long trackId = -1;

    public void start(long trackId) {
        // MyLog.d(LOG_TAG, "start");
        this.trackId = trackId;
        if(binding!=null) {
            setDataVisibility(true);
            startObserve(trackId);
            setStartTime(System.currentTimeMillis());
            uiUpdater.run();
        }
        isRecording = true;
    }

    public void stop() {
        // MyLog.d(LOG_TAG, "stop");
        setDataVisibility(false);
        handler.removeCallbacks(uiUpdater);
        // resetData();
    }

    private void startObserve(long trackId) {
        // MyLog.d(LOG_TAG, "startObserve");
        trackViewModel.init(trackId);
        trackViewModel.getTrack().observe(this, trackEntityObserver);
    }

    Observer<TrackEntity> trackEntityObserver = new Observer<TrackEntity>() {
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

    private void updateUi() {
        // // MyLog.d(LOG_LOG_TAG, "updateUi");
        if (startTime != 0) {
            setElapsedTime(System.currentTimeMillis() - startTime);
        }
    }

    private void setDataVisibility(boolean isRecording) {
        // MyLog.d(LOG_TAG, "setDataVisibility");
        if(binding == null){
            return;
        }
        // TODO: ezt valami animációval
        if (isRecording) {
            resetData();
            binding.tvPlaceholder.setVisibility(View.INVISIBLE);
            binding.distanceView.setVisibility(View.VISIBLE);
            binding.ascentView.setVisibility(View.VISIBLE);
            binding.descentView.setVisibility(View.VISIBLE);
            binding.elapsedTimeView.setVisibility(View.VISIBLE);
            binding.minAltitudeView.setVisibility(View.VISIBLE);
            binding.maxAltitudeView.setVisibility(View.VISIBLE);
            binding.avgSpeedView.setVisibility(View.VISIBLE);
            binding.maxSpeedView.setVisibility(View.VISIBLE);
        } else {
            binding.tvPlaceholder.setVisibility(View.VISIBLE);
            binding.distanceView.setVisibility(View.INVISIBLE);
            binding.ascentView.setVisibility(View.INVISIBLE);
            binding.descentView.setVisibility(View.INVISIBLE);
            binding.elapsedTimeView.setVisibility(View.INVISIBLE);
            binding.minAltitudeView.setVisibility(View.INVISIBLE);
            binding.maxAltitudeView.setVisibility(View.INVISIBLE);
            binding.avgSpeedView.setVisibility(View.INVISIBLE);
            binding.maxSpeedView.setVisibility(View.INVISIBLE);
        }
    }

    private void resetData() {
        // MyLog.d(LOG_TAG, "resetData");

        startTime = 0;

        if(binding == null){
            return;
        }

        binding.distanceView.setValue(getActivity().getString(R.string.distance_view_value));
        binding.distanceView.setUnit(getActivity().getString(R.string.distance_view_unit));
        binding.distanceView.setLabel(getActivity().getString(R.string.distance_view_label));

        binding.ascentView.setValue(getActivity().getString(R.string.ascent_view_value));
        binding.ascentView.setUnit(getActivity().getString(R.string.ascent_view_unit));
        binding.ascentView.setLabel(getActivity().getString(R.string.ascent_view_label));

        binding.descentView.setValue(getActivity().getString(R.string.descent_view_value));
        binding.descentView.setUnit(getActivity().getString(R.string.descent_view_unit));
        binding.descentView.setLabel(getActivity().getString(R.string.descent_view_label));

        binding.elapsedTimeView.setValue(getActivity().getString(R.string.elapsed_time_view_value));
        binding.elapsedTimeView.setUnit(getActivity().getString(R.string.elapsed_time_view_unit));
        binding.elapsedTimeView.setLabel(getActivity().getString(R.string.elapsed_time_view_label));

        binding.minAltitudeView.setValue(getActivity().getString(R.string.min_altitude_view_value));
        binding.minAltitudeView.setUnit(getActivity().getString(R.string.min_altitude_view_unit));
        binding.minAltitudeView.setLabel(getActivity().getString(R.string.min_altitude_view_label));

        binding.maxAltitudeView.setValue(getActivity().getString(R.string.max_altitude_view_value));
        binding.maxAltitudeView.setUnit(getActivity().getString(R.string.max_altitude_view_unit));
        binding.maxAltitudeView.setLabel(getActivity().getString(R.string.max_altitude_view_label));

        binding.avgSpeedView.setValue(getActivity().getString(R.string.avg_speed_view_value));
        binding.avgSpeedView.setUnit(getActivity().getString(R.string.avg_speed_view_unit));
        binding.avgSpeedView.setLabel(getActivity().getString(R.string.avg_speed_view_label));

        binding.maxSpeedView.setValue(getActivity().getString(R.string.max_speed_view_value));
        binding.maxSpeedView.setUnit(getActivity().getString(R.string.max_speed_view_unit));
        binding.maxSpeedView.setLabel(getActivity().getString(R.string.max_speed_view_label));
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    private void setDistance(double distance) {
        binding.distanceView.setValue(StringUtils.getDistanceAsThreeCharactersString(distance));
    }

    private void setAscent(double ascent) {
        binding.ascentView.setValue(String.format(Locale.getDefault(), "%.0f", ascent));
    }

    private void setDescent(double descent) {
        binding.descentView.setValue(String.format(Locale.getDefault(), "%.0f", descent));
    }

    private void setElapsedTime(long elapsedTime) {
        binding.elapsedTimeView.setValue(StringUtils.getElapsedTimeAsString(elapsedTime));
    }

    private void setMinAltitude(double minAltitude) {
        String minAltitudeString = String.format(Locale.getDefault(), "%.0f", minAltitude);
        binding.minAltitudeView.setValue(minAltitudeString);
    }

    private void setMaxAltitude(double maxAltitude) {
        String maxAltitudeString = String.format(Locale.getDefault(), "%.0f", maxAltitude);
        binding.maxAltitudeView.setValue(maxAltitudeString);
    }

    private void setMaxSpeed(double maxSpeed) {
        binding.maxSpeedView.setValue(StringUtils.getSpeedAsThreeCharactersString(maxSpeed));
    }

    private void setAvgSpeed(double avgSpeed) {
        binding.avgSpeedView.setValue(StringUtils.getSpeedAsThreeCharactersString(avgSpeed));
    }
}
