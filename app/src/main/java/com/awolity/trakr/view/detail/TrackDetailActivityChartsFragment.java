package com.awolity.trakr.view.detail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.awolity.trakr.R;
import com.awolity.trakr.customviews.PrimaryPropertyViewIcon;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.utils.StringUtils;
import com.awolity.trakr.viewmodel.TrackViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrackDetailActivityChartsFragment extends Fragment implements OnChartValueSelectedListener {

    private static final String ARG_TRACK_ID = "track_id";
    private static final String LOG_TAG = TrackDetailActivityChartsFragment.class.getSimpleName();

    private long trackId;
    private TrackViewModel trackViewModel;
    private PrimaryPropertyViewIcon maxSpeedPpvi, avgSpeedPpvi, ascentPpvi, descentPpvi,
            maxAltitudePpvi, minAltitudePpvi;
    private ImageView speedInitialImageView, elevationInitialImageView;
    private LineChart speedChart, elevationChart;

    public static TrackDetailActivityChartsFragment newInstance(long trackId) {
        TrackDetailActivityChartsFragment fragment = new TrackDetailActivityChartsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TRACK_ID, trackId);
        fragment.setArguments(args);
        return fragment;
    }

    public TrackDetailActivityChartsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            trackId = getArguments().getLong(ARG_TRACK_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_track_detail_fragment_charts, container, false);
        setupWidgets(view);
        resetWidgets();
        setupViewModel();
        return view;
    }

    private void setupWidgets(View view) {
        speedInitialImageView = view.findViewById(R.id.iv_initial_speed);
        elevationInitialImageView = view.findViewById(R.id.iv_initial_elevation);
        maxSpeedPpvi = view.findViewById(R.id.ppvi_max_speed);
        avgSpeedPpvi = view.findViewById(R.id.ppvi_avg_speed);
        speedChart = view.findViewById(R.id.chart_speed);
        elevationChart = view.findViewById(R.id.chart_elevation);
        ascentPpvi = view.findViewById(R.id.ppvi_ascent);
        descentPpvi = view.findViewById(R.id.ppvi_descent);
        maxAltitudePpvi = view.findViewById(R.id.ppvi_max_altitude);
        minAltitudePpvi = view.findViewById(R.id.ppvi_min_altitude);
    }

    private void resetWidgets() {
        // TODO: extract
        maxSpeedPpvi.setup("Max.Speed", "km/h", "0", R.drawable.ic_max_speed);
        avgSpeedPpvi.setup("Avg.Speed", "km/h", "-", R.drawable.ic_avg_speed);
        ascentPpvi.setup("Ascent", "m", "0", R.drawable.ic_ascent);
        descentPpvi.setup("Descent", "m", "0", R.drawable.ic_descent);
        maxAltitudePpvi.setup("Max.Altitude", "m", "-", R.drawable.ic_max_altitude);
        minAltitudePpvi.setup("Min.Altitude", "m", "-", R.drawable.ic_min_altitude);

        speedChart.setOnChartValueSelectedListener(this);
        Description description = new Description();
        description.setText("");
        speedChart.setDescription(description);
        // enable touch gestures
        speedChart.setTouchEnabled(true);
        // enable scaling and dragging
        speedChart.setDragEnabled(true);
        speedChart.setScaleEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        speedChart.setPinchZoom(false);
        Legend l = speedChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        elevationChart.setOnChartValueSelectedListener(this);
        elevationChart.setDescription(description);
        // enable touch gestures
        elevationChart.setTouchEnabled(true);
        // enable scaling and dragging
        elevationChart.setDragEnabled(true);
        elevationChart.setScaleEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        elevationChart.setPinchZoom(false);
        l = elevationChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
    }

    private void setupViewModel() {
        trackViewModel = ViewModelProviders.of(getActivity()).get(TrackViewModel.class);
        trackViewModel.getTrackWithPoints().observe(this, new Observer<TrackWithPoints>() {
            @Override
            public void onChanged(@Nullable TrackWithPoints trackWithPoints) {
                if (trackWithPoints != null) {
                    setData(trackWithPoints);
                    setChartData(trackWithPoints);
                }
            }
        });
    }

    private void setData(TrackWithPoints trackWithPoints) {
        MyLog.d(LOG_TAG, "setData");

        ColorGenerator generator = ColorGenerator.MATERIAL;
        TextDrawable drawable = TextDrawable.builder()
                .buildRound("S", generator.getColor(trackWithPoints.getTitle()));
        speedInitialImageView.setImageDrawable(drawable);

        drawable = TextDrawable.builder()
                .buildRound("E", generator.getColor(trackWithPoints.getTitle()));
        elevationInitialImageView.setImageDrawable(drawable);

        maxSpeedPpvi.setValue(StringUtils.getSpeedAsThreeCharactersString(trackWithPoints.getMaxSpeed()));
        avgSpeedPpvi.setValue(StringUtils.getSpeedAsThreeCharactersString(trackWithPoints.getAvgSpeed()));
        ascentPpvi.setValue(String.format(Locale.getDefault(), "%.0f", trackWithPoints.getAscent()));
        descentPpvi.setValue(String.format(Locale.getDefault(), "%.0f", trackWithPoints.getDescent()));
        minAltitudePpvi.setValue(String.format(Locale.getDefault(), "%.0f", trackWithPoints.getMinAltitude()));
        maxAltitudePpvi.setValue(String.format(Locale.getDefault(), "%.0f", trackWithPoints.getMaxAltitude()));
    }

    private void setChartData(TrackWithPoints trackWithPoints) {
        LineDataSet speedDataSet = new LineDataSet(prepareSpeedData(trackWithPoints), "Speed [km/h]");
        LineDataSet elevationDataSet = new LineDataSet(prepareElevationData(trackWithPoints), "Elevation [m]");

        speedDataSet.setDrawIcons(false);
        speedDataSet.setDrawValues(false);
        speedDataSet.setColor(getResources().getColor(R.color.colorAccent));
        speedDataSet.setDrawCircles(false);
        speedDataSet.setLineWidth(3f);
        speedDataSet.setValueTextSize(9f);
        speedDataSet.setDrawFilled(true);
        speedDataSet.setFormLineWidth(1f);
        speedDataSet.setFormSize(15.f);
        speedDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        elevationDataSet.setDrawIcons(false);
        elevationDataSet.setDrawValues(false);
        elevationDataSet.setColor(getResources().getColor(R.color.colorPrimary));
        elevationDataSet.setDrawCircles(false);
        elevationDataSet.setLineWidth(3f);
        elevationDataSet.setValueTextSize(9f);
        elevationDataSet.setDrawFilled(true);
        elevationDataSet.setFormLineWidth(1f);
        elevationDataSet.setFormSize(15.f);
        elevationDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_accent_color);
        speedDataSet.setFillDrawable(drawable);

        drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_primary_color);
        elevationDataSet.setFillDrawable(drawable);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(speedDataSet);
        LineData data = new LineData(dataSets);
        speedChart.setData(data);
        speedChart.invalidate();

        dataSets = new ArrayList<>();
        dataSets.add(elevationDataSet);
        data = new LineData(dataSets);
        elevationChart.setData(data);
        elevationChart.invalidate();
    }

    private List<Entry> prepareSpeedData(TrackWithPoints trackWithPoints) {
        List<Entry> values = new ArrayList<>();
        List<TrackpointEntity> trackpointEntityList = trackWithPoints.getTrackPoints();
        long startTime = trackWithPoints.getStartTime();
        long durationInSeconds = (trackpointEntityList.get(trackpointEntityList.size() - 1).getTime()
                - startTime)
                / 1000;

        for (TrackpointEntity trackpointEntity : trackpointEntityList) {
            long elapsedSeconds = (trackpointEntity.getTime() - startTime) / 1000;
            values.add(new Entry((float) elapsedSeconds, (float) trackpointEntity.getSpeed()));
        }
        speedChart.getXAxis().setValueFormatter(new GraphTimeAxisValueFormatter(durationInSeconds));
        return values;
    }

    private List<Entry> prepareElevationData(TrackWithPoints trackWithPoints) {
        List<Entry> values = new ArrayList<>();
        List<TrackpointEntity> trackpointEntityList = trackWithPoints.getTrackPoints();
        long startTime = trackWithPoints.getStartTime();
        long durationInSeconds = (trackpointEntityList.get(trackpointEntityList.size() - 1).getTime()
                - startTime)
                / 1000;

        for (TrackpointEntity trackpointEntity : trackpointEntityList) {
            long elapsedSeconds = (trackpointEntity.getTime() - startTime) / 1000;
            values.add(new Entry((float) elapsedSeconds, (float) trackpointEntity.getAltitude()));
        }
        speedChart.getXAxis().setValueFormatter(new GraphTimeAxisValueFormatter(durationInSeconds));
        return values;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
