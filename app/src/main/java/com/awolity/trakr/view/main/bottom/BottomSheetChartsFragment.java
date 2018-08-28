package com.awolity.trakr.view.main.bottom;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.viewmodel.TrackViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetChartsFragment extends BottomSheetBaseFragment {

    private static final String TAG = BottomSheetChartsFragment.class.getSimpleName();
    private TrackViewModel trackViewModel;
    private boolean isRecording;
    private long trackId = -1;
    private LineChart chart;
    private TextView placeholderTextView;
    private TrackWithPoints trackWithPoints;
    private Handler handler;
    private Runnable chartUpdater;
    private boolean firstRun;

    public static BottomSheetChartsFragment newInstance(String title) {
        BottomSheetChartsFragment fragment = new BottomSheetChartsFragment();
        fragment.setTitle(title);
        return fragment;
    }

    public BottomSheetChartsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // MyLog.d(TAG, "onCreate");
        //noinspection ConstantConditions
        trackViewModel = ViewModelProviders.of(getActivity()).get(TrackViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.activity_main_fragment_bottom_sheet_chart, container, false);
        setupWidgets(view);
        setupChart();
        setupChartUpdater();
        setDataVisibility(false);

        if (isRecording && trackId != -1) {
            startTrackDataUpdate(trackId);
        }

        return view;
    }

    private void setupChartUpdater() {
        handler = new Handler();
        chartUpdater = new Runnable() {
            @Override
            public void run() {
                updateChart();
                handler.postDelayed(chartUpdater, 10000);
            }
        };
    }

    private void setupWidgets(View view) {
        chart = view.findViewById(R.id.chart);
        placeholderTextView = view.findViewById(R.id.tvPlaceholder);
    }

    private void setupChart() {
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
    }

    @SuppressWarnings("ConstantConditions")
    private void startObserve(/*long trackId*/) {
        // MyLog.d(TAG, "startObserve");
        trackViewModel.getTrackWithPoints().observe(getActivity(), trackWithPointsObserver);
    }

    private final Observer<TrackWithPoints> trackWithPointsObserver = new Observer<TrackWithPoints>() {
        @Override
        public void onChanged(@Nullable TrackWithPoints trackWithPoints) {
            // MyLog.d(TAG, "trackWithPointsObserver.onChanged");
            if (trackWithPoints != null) {
                BottomSheetChartsFragment.this.trackWithPoints = trackWithPoints;
                if (firstRun) {
                    updateChart();
                    firstRun = false;
                }
            }
        }
    };

    private void stopObserve() {
        // MyLog.d(TAG, "stopObserve");
        trackViewModel.getTrackWithPoints().removeObserver(trackWithPointsObserver);
    }

    private void startChartUpdater() {
        // MyLog.d(TAG, "startChartUpdater");
        updateChart();
        chartUpdater.run();
    }

    private void stopChartUpdater() {
        // MyLog.d(TAG, "stopChartUpdater");
        handler.removeCallbacks(chartUpdater);
    }

    public void startTrackDataUpdate(long trackId) {
        // MyLog.d(TAG, "startTrackDataUpdate");
        this.trackId = trackId;
        if (checkViews()) {
            setDataVisibility(true);
            startObserve(/*trackId*/);
            startChartUpdater();
        }
        isRecording = true;
    }

    public void stopTrackDataUpdate() {
        // MyLog.d(TAG, "stopTrackDataUpdate");
        setDataVisibility(false);
        stopObserve();
        stopChartUpdater();
        trackWithPoints = null;
        chart.clear();
    }

    private void updateChart() {
        // MyLog.d(TAG, "updateChart");
        if (trackWithPoints == null) {
            // MyLog.d(TAG, "updateChart - track NULL :(");
            return;
        } else {
            if (trackWithPoints.getTrackPoints().size() < 3) {
                return;
            }
        }
        // MyLog.d(TAG, "updateChart - track NOT null");
        List<Entry> elevationValues = new ArrayList<>();
        List<Entry> speedValues = new ArrayList<>();
        List<TrackpointEntity> trackpointEntityList = trackWithPoints.getTrackPoints();
        double rollingDistance = 0;

        for (TrackpointEntity trackpointEntity : trackpointEntityList) {
            rollingDistance += trackpointEntity.getDistance();
            elevationValues.add(new Entry((float) rollingDistance, (float) trackpointEntity.getAltitude()));
            speedValues.add(new Entry((float) rollingDistance, (float) trackpointEntity.getSpeed()));
        }
        chart.getXAxis().setValueFormatter(new LargeValueFormatter());
        LineDataSet elevationDataSet = new LineDataSet(elevationValues, getString(R.string.elevation_chart_title));
        LineDataSet speedDataSet = new LineDataSet(speedValues, getString(R.string.speed_chart_title));
        speedDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        chart.getAxisRight().setAxisMinimum(0);

        elevationDataSet.setDrawIcons(false);
        elevationDataSet.setDrawValues(false);
        elevationDataSet.setColor(getResources().getColor(R.color.colorPrimary));
        elevationDataSet.setDrawCircles(false);
        elevationDataSet.setLineWidth(3f);
        elevationDataSet.setValueTextSize(9f);
        elevationDataSet.setDrawFilled(true);
        elevationDataSet.setFormLineWidth(1f);
        elevationDataSet.setFormSize(15.f);
        //noinspection ConstantConditions
        Drawable elevationFillDrawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_primary_color);
        elevationDataSet.setFillDrawable(elevationFillDrawable);

        speedDataSet.setDrawIcons(false);
        speedDataSet.setDrawValues(false);
        speedDataSet.setColor(getResources().getColor(R.color.colorAccent));
        speedDataSet.setDrawCircles(false);
        speedDataSet.setLineWidth(3f);
        speedDataSet.setValueTextSize(9f);
        speedDataSet.setDrawFilled(true);
        speedDataSet.setFormLineWidth(1f);
        speedDataSet.setFormSize(15.f);
        Drawable speedFillDrawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_accent_color);
        speedDataSet.setFillDrawable(speedFillDrawable);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(elevationDataSet);
        dataSets.add(speedDataSet);
        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate();
    }

    private void setDataVisibility(boolean isRecording) {
        // MyLog.d(TAG, "setDataVisibility");
        // TODO: ezt valami animációval
        if (isRecording) {
            //resetData();
            placeholderTextView.setVisibility(View.INVISIBLE);
            chart.setVisibility(View.VISIBLE);

        } else {
            placeholderTextView.setVisibility(View.VISIBLE);
            chart.setVisibility(View.INVISIBLE);
        }
    }

    private boolean checkViews() {
        return chart != null;
    }

    @Override
    public void onStart() {
        super.onStart();
        firstRun = true;
        if (isRecording) {
            startChartUpdater();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {
            stopChartUpdater();
        }
    }
}
