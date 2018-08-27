package com.awolity.trakr.view.detail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.viewmodel.TrackViewModel;
import com.awolity.trakrutils.StringUtils;
import com.awolity.trakrutils.Utility;
import com.awolity.trakrviews.PrimaryPropertyViewIcon;

import java.util.Locale;

public class TrackDetailActivityDataFragment extends Fragment {

    private static final String ARG_ICON = "arg_icon";
    private static final String TAG = TrackDetailActivityDataFragment.class.getSimpleName();

    private PrimaryPropertyViewIcon durationPpvi, distancePpvi, ascentPpvi, descentPpvi,
            maxSpeedPpvi, avgSpeedPpvi, maxAltitudePpvi, minAltitudePpvi, startTimePpvi,
            endTimePpvi, avgPacePpvi, maxPacePpvi;
    private TextView titleTextView, dateTextView;
    private ImageButton editTitleImageButton;
    private ImageView initialImageView;

    public static TrackDetailActivityDataFragment newInstance(Bitmap icon) {
        TrackDetailActivityDataFragment fragment = new TrackDetailActivityDataFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ICON, icon);
        fragment.setArguments(args);
        return fragment;
    }

    public TrackDetailActivityDataFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_track_detail_fragment_data,
                container, false);
        setupWidgets(view);
        resetWidgets();
        setupViewModel();
        return view;
    }

    private void setupWidgets(View view) {
        initialImageView = view.findViewById(R.id.iv_icon);
        editTitleImageButton = view.findViewById(R.id.ib_edit);
        titleTextView = view.findViewById(R.id.tv_title);
        dateTextView = view.findViewById(R.id.tv_date);
        startTimePpvi = view.findViewById(R.id.ppvi_start_time);
        endTimePpvi = view.findViewById(R.id.ppvi_end_time);
        durationPpvi = view.findViewById(R.id.ppvi_duration);
        distancePpvi = view.findViewById(R.id.ppvi_distance);
        ascentPpvi = view.findViewById(R.id.ppvi_ascent);
        descentPpvi = view.findViewById(R.id.ppvi_descent);
        maxSpeedPpvi = view.findViewById(R.id.ppvi_max_speed);
        avgSpeedPpvi = view.findViewById(R.id.ppvi_avg_speed);
        maxPacePpvi = view.findViewById(R.id.ppvi_max_pace);
        avgPacePpvi = view.findViewById(R.id.ppvi_avg_pace);
        maxAltitudePpvi = view.findViewById(R.id.ppvi_max_altitude);
        minAltitudePpvi = view.findViewById(R.id.ppvi_min_altitude);
    }

    private void resetWidgets() {
        startTimePpvi.setup(getString(R.string.start_time_view_title),
                getString(R.string.start_time_view_unit),
                getString(R.string.start_time_view_default_value),
                R.drawable.ic_start_time);
        endTimePpvi.setup(getString(R.string.end_time_view_title),
                getString(R.string.end_time_view_unit),
                getString(R.string.end_time_view_default_value),
                R.drawable.ic_end_time);
        durationPpvi.setup(getString(R.string.elapsed_time_view_title),
                getString(R.string.elapsed_time_view_unit),
                getString(R.string.elapsed_time_view_default_value),
                R.drawable.ic_duration);
        distancePpvi.setup(getString(R.string.distance_view_title),
                getString(R.string.distance_view_unit),
                getString(R.string.distance_view_default_value),
                R.drawable.ic_distance);
        maxSpeedPpvi.setup(getString(R.string.max_speed_view_title),
                getString(R.string.max_speed_view_unit),
                getString(R.string.max_speed_view_default_value),
                R.drawable.ic_max_speed);
        avgSpeedPpvi.setup(getString(R.string.avg_speed_view_title),
                getString(R.string.avg_speed_view_unit),
                getString(R.string.avg_speed_view_default_value),
                R.drawable.ic_avg_speed);
        maxPacePpvi.setup(getString(R.string.max_pace_view_title),
                getString(R.string.max_pace_view_unit),
                getString(R.string.max_pace_view_default_value),
                R.drawable.ic_max_speed);
        avgPacePpvi.setup(getString(R.string.avg_pace_view_title),
                getString(R.string.avg_pace_view_unit),
                getString(R.string.avg_pace_view_default_value),
                R.drawable.ic_avg_speed);
        ascentPpvi.setup(getString(R.string.ascent_view_title),
                getString(R.string.ascent_view_unit),
                getString(R.string.ascent_view_default_value),
                R.drawable.ic_ascent);
        descentPpvi.setup(getString(R.string.descent_view_title),
                getString(R.string.descent_view_unit),
                getString(R.string.descent_view_default_value),
                R.drawable.ic_descent);
        maxAltitudePpvi.setup(getString(R.string.max_altitude_view_title),
                getString(R.string.max_altitude_view_unit),
                getString(R.string.max_altitude_view_default_value),
                R.drawable.ic_max_altitude);
        minAltitudePpvi.setup(getString(R.string.min_altitude_view_title),
                getString(R.string.min_altitude_view_unit),
                getString(R.string.min_altitude_view_default_value),
                R.drawable.ic_min_altitude);
    }

    private void setupViewModel() {
        TrackViewModel trackViewModel = ViewModelProviders.of(getActivity())
                .get(TrackViewModel.class);
        trackViewModel.getTrackWithPoints().observe(this, new Observer<TrackWithPoints>() {
            @Override
            public void onChanged(@Nullable final TrackWithPoints trackWithPoints) {
                if (trackWithPoints != null) {
                    setData(trackWithPoints);

                    editTitleImageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EditTitleDialog dialog = EditTitleDialog.newInstance(
                                    trackWithPoints.getTitle());
                            dialog.show(getActivity().getSupportFragmentManager(), null);
                        }
                    });
                    getActivity().startPostponedEnterTransition();
                }
            }
        });
    }

    private void setData(TrackWithPoints trackWithPoints) {
        // MyLog.d(TAG, "setData");

        String firstLetter = "";
        if (trackWithPoints.getTitle() != null && !trackWithPoints.getTitle().isEmpty()) {
            firstLetter = trackWithPoints.getTitle().substring(0, 1);
        }

        initialImageView.setImageDrawable(
                Utility.getInitial(firstLetter, String.valueOf(trackWithPoints.getStartTime()),
                        initialImageView.getLayoutParams().width));

        titleTextView.setText(trackWithPoints.getTitle());
        dateTextView.setText(StringUtils.getDateAsStringLocale(trackWithPoints.getStartTime()));
        startTimePpvi.setValue(StringUtils.getTimeAsString(trackWithPoints.getStartTime()));
        endTimePpvi.setValue(StringUtils.getTimeAsString(trackWithPoints.getStartTime()
                + trackWithPoints.getElapsedTime()));
        durationPpvi.setValue(StringUtils.getElapsedTimeAsString(trackWithPoints.getElapsedTime()));
        distancePpvi.setValue(StringUtils.getDistanceAsThreeCharactersString(
                trackWithPoints.getDistance()));
        ascentPpvi.setValue(String.format(Locale.getDefault(), "%.0f",
                trackWithPoints.getAscent()));
        descentPpvi.setValue(String.format(Locale.getDefault(), "%.0f",
                trackWithPoints.getDescent()));
        maxSpeedPpvi.setValue(StringUtils.getSpeedAsThreeCharactersString(
                trackWithPoints.getMaxSpeed()));
        avgSpeedPpvi.setValue(StringUtils.getSpeedAsThreeCharactersString(
                trackWithPoints.getAvgSpeed()));
        double maxSpeed = trackWithPoints.getMaxSpeed();
        if (maxSpeed > 1) {
            maxPacePpvi.setValue(StringUtils.getSpeedAsThreeCharactersString((60 * (1 / maxSpeed))));
        } else {
            maxPacePpvi.setValue("-");
        }
        double avgSpeed = trackWithPoints.getAvgSpeed();
        if (avgSpeed > 1) {
            avgPacePpvi.setValue(StringUtils.getSpeedAsThreeCharactersString((60 * (1 / avgSpeed))));
        } else {
            avgPacePpvi.setValue("-");
        }
        minAltitudePpvi.setValue(String.format(Locale.getDefault(), "%.0f",
                trackWithPoints.getMinAltitude()));
        maxAltitudePpvi.setValue(String.format(Locale.getDefault(), "%.0f",
                trackWithPoints.getMaxAltitude()));
    }
}
