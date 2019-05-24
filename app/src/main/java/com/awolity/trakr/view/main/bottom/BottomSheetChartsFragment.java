package com.awolity.trakr.view.main.bottom;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.awolity.trakr.R;
import com.awolity.trakr.model.ChartPoint;
import com.awolity.trakr.view.main.MainActivityViewModel;
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

    private MainActivityViewModel mainActivityViewModel;
    private boolean isRecording;
    private long trackId = -1;
    private LineChart chart;
    private TextView placeholderTextView;
    private Handler handler;
    private Runnable chartUpdater;
    private boolean firstRun;
    private List<ChartPoint> chartPoints;

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
        mainActivityViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
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
        chartUpdater = () -> {
            updateChart();
            handler.postDelayed(chartUpdater, 10000);
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
        mainActivityViewModel.getChartPoints().observe(getActivity(), chartPointsObserver);
    }

    private final Observer<List<ChartPoint>> chartPointsObserver = new Observer<List<ChartPoint>>() {
        @Override
        public void onChanged(@Nullable List<ChartPoint> chartPoints) {
            // MyLog.d(TAG, "trackWithPointsObserver.onChanged");
            if (chartPoints != null) {
                BottomSheetChartsFragment.this.chartPoints = chartPoints;
                if (firstRun) {
                    updateChart();
                    firstRun = false;
                }
            }
        }
    };

    private void stopObserve() {
        // MyLog.d(TAG, "stopObserve");
        mainActivityViewModel.getChartPoints().removeObserver(chartPointsObserver);
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
        chartPoints = null;
        chart.clear();
    }

    private void updateChart() {
        // MyLog.d(TAG, "updateChart");
        if (chartPoints == null) {
            // MyLog.d(TAG, "updateChart - track NULL :(");
            return;
        } else {
            if (chartPoints.size() < 3) {
                return;
            }
        }
        // MyLog.d(TAG, "updateChart - track NOT null");
        List<Entry> elevationValues = new ArrayList<>();
        List<Entry> speedValues = new ArrayList<>();
        double rollingDistance = 0;

        for (ChartPoint chartPoint : chartPoints) {
            rollingDistance += chartPoint.getDistance();
            elevationValues.add(new Entry((float) rollingDistance, (float) chartPoint.getAltitude()));
            speedValues.add(new Entry((float) rollingDistance, (float) chartPoint.getSpeed()));
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
