package com.awolity.trakr.view.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.viewmodel.TrackViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetChartsFragment extends BottomSheetBaseFragment implements OnChartValueSelectedListener {

    private static final String LOG_TAG = BottomSheetChartsFragment.class.getSimpleName();
    private TrackViewModel trackViewModel;
    private boolean isRecording;
    private long trackId = -1;
    private LineChart chart;
    private TextView placeholderTextView;

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
        // MyLog.d(LOG_TAG, "onCreate");
        //noinspection ConstantConditions
        trackViewModel = ViewModelProviders.of(getActivity()).get(TrackViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.activity_main_fragment_bottom_sheet_chart, container, false);
        setupWidgets(view);
        setupChart();
        setDataVisibility(false);

        if(isRecording &&trackId != -1){
            startTrackDataUpdate(trackId);
        }

        return view;
    }

    private void setupWidgets(View view) {
        chart = view.findViewById(R.id.chart);
        placeholderTextView = view.findViewById(R.id.tvPlaceholder);
    }

    private void setupChart() {
        chart.setOnChartValueSelectedListener(this);

        chart.setDrawGridBackground(true);
        // TODO: grid rácsköz?
        chart.getDescription().setEnabled(true);
        // TODO: description?
        chart.setDrawBorders(false);
        // TODO: megnézni

        chart.getAxisLeft().setEnabled(true);
        chart.getAxisRight().setDrawAxisLine(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawAxisLine(true);
        chart.getXAxis().setDrawGridLines(true);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
    }

    public void startTrackDataUpdate(long trackId) {
        MyLog.d(LOG_TAG, "startTrackDataUpdate");
        this.trackId = trackId;
        if (checkViews()) {
            setDataVisibility(true);
            startObserve(trackId);
        }
        isRecording = true;
    }

    public void stopTrackDataUpdate() {
        MyLog.d(LOG_TAG, "stopTrackDataUpdate");
        setDataVisibility(false);
    }

    private void startObserve(long trackId) {
        // MyLog.d(LOG_TAG, "startObserve");
        trackViewModel.init(trackId);
        trackViewModel.getTrackWithPoints().observe(this, new Observer<TrackWithPoints>() {
            @Override
            public void onChanged(@Nullable TrackWithPoints trackWithPoints) {
                // MyLog.d(LOG_TAG, "trackWithPointsObserver.onChanged");
                if (trackWithPoints != null) {
                    // MyLog.d(LOG_TAG, "trackWithPointsObserver.onChanged - track NOT null");
                    LineDataSet speedDataSet = new LineDataSet(prepareSpeedData(trackWithPoints), "Speed");
                    ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                    dataSets.add(speedDataSet);
                    LineData data = new LineData(dataSets);
                    chart.setData(data);
                    chart.invalidate();
                }
            }
        });
    }

    private List<Entry> prepareSpeedData(TrackWithPoints trackWithPoints) {
        List<Entry> values = new ArrayList<>();

        long previousElapsedSeconds = 0;
        values.add(new Entry(previousElapsedSeconds, 0));
        for (TrackpointEntity trackpointEntity : trackWithPoints.getTrackPoints()) {
            // if we already created an entry with the same time values(second)
            // then don't do it again
            long elapsedSeconds = getElapsedSeconds(trackWithPoints.getStartTime(), trackpointEntity.getTime());
            if (elapsedSeconds != previousElapsedSeconds) {
                values.add(new Entry((float) elapsedSeconds, (float) trackpointEntity.getSpeed()));
            }
        }
        return values;
    }

    private long getElapsedSeconds(long startTime, long pointTime) {
        return pointTime - startTime / 1000;
    }

    private void setDataVisibility(boolean isRecording) {
        // MyLog.d(LOG_TAG, "setDataVisibility");
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

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        // MyLog.d(LOG_TAG, "onValueSelected");
    }

    @Override
    public void onNothingSelected() {
        // MyLog.d(LOG_TAG, "onNothingSelected");
    }

    private boolean checkViews() {
        return chart != null;
    }
}
