package com.awolity.trakr.view.detail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.animation.EasingFunction;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class TrackDetailActivityChartsFragment extends Fragment implements OnChartValueSelectedListener {

    private static final String ARG_TRACK_ID = "track_id";
    private static final String LOG_TAG = TrackDetailActivityChartsFragment.class.getSimpleName();

    private long trackId;
    private TrackViewModel trackViewModel;
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
        initialImageView = view.findViewById(R.id.iv_initial);
        maxSpeedPpvi = view.findViewById(R.id.ppvi_max_speed);
        avgSpeedPpvi = view.findViewById(R.id.ppvi_avg_speed);
        speedChart = view.findViewById(R.id.chart_speed);
    }

    private void resetWidgets() {
        // TODO: extract
        maxSpeedPpvi.setup("Max.Speed", "km/h", "0", R.drawable.ic_max_speed);
        avgSpeedPpvi.setup("Avg.Speed", "km/h", "-", R.drawable.ic_avg_speed);

        speedChart.setOnChartValueSelectedListener(this);
        // speedChart.setDrawGridBackground(false);
        Description description = new Description();
        description.setText("");
        speedChart.setDescription(description);
        // TODO: grid rácsköz?
        // TODO: description?
        /*speedChart.setDrawBorders(false);
        speedChart.getAxisLeft().setEnabled(true);
        speedChart.getAxisRight().setDrawAxisLine(false);
        speedChart.getAxisRight().setDrawGridLines(false);
        speedChart.getXAxis().setDrawAxisLine(true);
        speedChart.getXAxis().setDrawGridLines(true);*/
        // enable touch gestures
        speedChart.setTouchEnabled(true);
        // enable scaling and dragging
        speedChart.setDragEnabled(true);
        speedChart.setScaleEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        speedChart.setPinchZoom(false);


        XAxis xAxis = speedChart.getXAxis();
        //xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        //xAxis.setDrawGridLines(false);
        //xAxis.setGranularity(1f); // only intervals of 1 day
        //xAxis.setTypeface(mTfLight);
        //xAxis.setTextSize(8);
        //xAxis.setTextColor(ContextCompat.getColor(this, R.color.colorYellow));
        //xAxis.setValueFormatter(new GraphTimeAxisValueFormatter(range, interval, slot));

        Legend l = speedChart.getLegend();
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
        initialImageView.setImageDrawable(drawable);

        maxSpeedPpvi.setValue(StringUtils.getSpeedAsThreeCharactersString(trackWithPoints.getMaxSpeed()));
        avgSpeedPpvi.setValue(StringUtils.getSpeedAsThreeCharactersString(trackWithPoints.getAvgSpeed()));
    }

    private void setChartData(TrackWithPoints trackWithPoints) {
        LineDataSet speedDataSet = new LineDataSet(prepareSpeedData(trackWithPoints), "Speed [km/h]");

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

        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_accent_color);
        speedDataSet.setFillDrawable(drawable);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(speedDataSet);
        LineData data = new LineData(dataSets);

        speedChart.setData(data);
        speedChart.invalidate();
    }

    private List<Entry> prepareSpeedData(TrackWithPoints trackWithPoints) {
        List<Entry> values = new ArrayList<>();
        List<TrackpointEntity> trackpointEntityList = trackWithPoints.getTrackPoints();
        long startTime = trackWithPoints.getStartTime();
        long durationInSeconds=  (trackpointEntityList.get(trackpointEntityList.size() - 1).getTime()
                - startTime)
                / 1000;

        for (TrackpointEntity trackpointEntity : trackpointEntityList) {
            long elapsedSeconds = (trackpointEntity.getTime() - startTime) / 1000;
            values.add(new Entry((float) elapsedSeconds, (float) trackpointEntity.getSpeed()));
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
