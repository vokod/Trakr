package com.awolity.trakr.view.detail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
    private TextView titleTextView;
    private PrimaryPropertyViewIcon maxSpeedPpvi, avgSpeedPpvi;
    private ImageView initialImageView;
    private LineChart speedChart;

    public static TrackDetailActivityChartsFragment newInstance(long trackId) {
        TrackDetailActivityChartsFragment fragment = new TrackDetailActivityChartsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TRACK_ID, trackId);
        fragment.setArguments(args);
        return fragment;
    }

    public TrackDetailActivityChartsFragment() { }

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
        initialImageView = view.findViewById(R.id.iv_initial);
        titleTextView = view.findViewById(R.id.tv_title);
        maxSpeedPpvi = view.findViewById(R.id.ppvi_max_speed);
        avgSpeedPpvi = view.findViewById(R.id.ppvi_avg_speed);
        speedChart = view.findViewById(R.id.chart_speed);
    }

    private void resetWidgets() {
        // TODO: extract
        maxSpeedPpvi.setup("Max.Speed", "km/h", "0", R.drawable.ic_max_speed);
        avgSpeedPpvi.setup("Avg.Speed", "km/h", "-", R.drawable.ic_avg_speed);

        speedChart.setOnChartValueSelectedListener(this);
        speedChart.setDrawGridBackground(true);
        // TODO: grid rácsköz?
        speedChart.getDescription().setEnabled(true);
        // TODO: description?
        speedChart.setDrawBorders(false);
        // TODO: megnézni
        speedChart.getAxisLeft().setEnabled(true);
        speedChart.getAxisRight().setDrawAxisLine(false);
        speedChart.getAxisRight().setDrawGridLines(false);
        speedChart.getXAxis().setDrawAxisLine(true);
        speedChart.getXAxis().setDrawGridLines(true);
        // enable touch gestures
        speedChart.setTouchEnabled(true);
        // enable scaling and dragging
        speedChart.setDragEnabled(true);
        speedChart.setScaleEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        speedChart.setPinchZoom(false);

        Legend l = speedChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
    }

    private void setupViewModel() {
        trackViewModel = ViewModelProviders.of(getActivity()).get(TrackViewModel.class);
        trackViewModel.getTrackWithPoints().observe(this, new Observer<TrackWithPoints>() {
            @Override
            public void onChanged(@Nullable TrackWithPoints trackWithPoints) {
                if (trackWithPoints != null) {
                    setData(trackWithPoints);
                    setChartData(trackWithPoints.getTrackPoints());
                }
            }
        });
    }

    private void setData(TrackWithPoints trackWithPoints) {
        MyLog.d(LOG_TAG, "setData");

        ColorGenerator generator = ColorGenerator.MATERIAL;
        TextDrawable drawable = TextDrawable.builder()
                .buildRound("S", generator.getColor(trackWithPoints.getTitle()));
        initialImageView.setImageDrawable(drawable);

        titleTextView.setText(trackWithPoints.getTitle());
        maxSpeedPpvi.setValue(StringUtils.getSpeedAsThreeCharactersString(trackWithPoints.getMaxSpeed()));
        avgSpeedPpvi.setValue(StringUtils.getSpeedAsThreeCharactersString(trackWithPoints.getAvgSpeed()));
    }

    private void setChartData(List<TrackpointEntity> trackpointEntityList){
        LineDataSet speedDataSet = new LineDataSet(prepareSpeedData(trackpointEntityList), "Speed");
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(speedDataSet);
        LineData data = new LineData(dataSets);
        speedChart.setData(data);
        speedChart.invalidate();
    }

    private List<Entry> prepareSpeedData(List<TrackpointEntity> trackpointEntityList) {
        List<Entry> values = new ArrayList<>();

        long previousElapsedSeconds = 0;
        values.add(new Entry(previousElapsedSeconds, 0));
        for (TrackpointEntity trackpointEntity : trackpointEntityList) {
            // if we already created an entry with the same time values(second)
            // then don't do it again
            long elapsedSeconds = getElapsedSeconds(trackpointEntityList.get(0).getTime(), trackpointEntity.getTime());
            if (elapsedSeconds != previousElapsedSeconds) {
                values.add(new Entry((float) elapsedSeconds, (float) trackpointEntity.getSpeed()));
            }
            previousElapsedSeconds = elapsedSeconds;
        }
        return values;
    }

    private long getElapsedSeconds(long startTime, long pointTime) {
        return pointTime - startTime / 1000;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
