package com.awolity.trakr.view.detail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.awolity.trakr.R;
import com.awolity.trakr.model.ChartPoint;
import com.awolity.trakr.model.TrackData;
import com.awolity.trakrutils.Constants;
import com.awolity.trakrutils.StringUtils;
import com.awolity.trakrutils.Utility;
import com.awolity.trakrviews.PrimaryPropertyViewIcon;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lib.kingja.switchbutton.SwitchMultiButton;

public class TrackDetailActivityChartsFragment extends Fragment {

    private PrimaryPropertyViewIcon maxSpeedPpvi, avgSpeedPpvi, ascentPpvi, descentPpvi,
            maxAltitudePpvi, minAltitudePpvi, maxPacePpvi, avgPacePpvi;
    private CheckBox paceCheckBox, speedCheckBox;
    private LineChart speedChart, elevationChart;
    private int xAxis = 1;
    private boolean isSpeed = true;
    private TrackData trackData;
    private List<ChartPoint> chartPoints;
    private TextView titleTextView, dateTextView;
    private ImageButton editTitleImageButton;
    private ImageView initialImageView;
    private TrackDetailViewModel trackDetailViewModel;
    private LineDataSet elevationDataSet;

    public static TrackDetailActivityChartsFragment newInstance() {
        return new TrackDetailActivityChartsFragment();
    }

    public TrackDetailActivityChartsFragment() {
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_track_detail_fragment_charts, container, false);
        setupWidgets(view);
        setupCharts();

        trackDetailViewModel = ViewModelProviders.of(getActivity())
                .get(TrackDetailViewModel.class);

        resetWidgets();
        observe();
        return view;
    }

    private void setupWidgets(View view) {
        SwitchMultiButton switchMultiButton = view.findViewById(R.id.smb_xaxis);
        switchMultiButton.setSelectedTab(xAxis);
        switchMultiButton.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String tabText) {
                if (xAxis != position) {
                    xAxis = position;
                    if (position == 0 && trackData != null && chartPoints != null) {
                        setElevationChartDataByTime(chartPoints);
                        if (isSpeed) {
                            setSpeedChartDataByTime(chartPoints);
                        } else {
                            setPaceChartDataByTime(chartPoints);
                        }
                    } else if (position == 1 && trackData != null && chartPoints != null) {
                        setElevationChartDataByDistance(chartPoints);
                        if (isSpeed) {
                            setSpeedChartDataByDistance(chartPoints);
                        } else {
                            setPaceChartDataByDistance(chartPoints);
                        }
                    }
                }
            }
        });

        initialImageView = view.findViewById(R.id.iv_icon);
        editTitleImageButton = view.findViewById(R.id.ib_edit);
        titleTextView = view.findViewById(R.id.tv_title);
        dateTextView = view.findViewById(R.id.tv_date);
        maxSpeedPpvi = view.findViewById(R.id.ppvi_max_speed);
        maxPacePpvi = view.findViewById(R.id.ppvi_max_pace);
        avgSpeedPpvi = view.findViewById(R.id.ppvi_avg_speed);
        avgPacePpvi = view.findViewById(R.id.ppvi_avg_pace);
        speedChart = view.findViewById(R.id.chart_speed);
        elevationChart = view.findViewById(R.id.chart_elevation);
        ascentPpvi = view.findViewById(R.id.ppvi_ascent);
        descentPpvi = view.findViewById(R.id.ppvi_descent);
        maxAltitudePpvi = view.findViewById(R.id.ppvi_max_altitude);
        minAltitudePpvi = view.findViewById(R.id.ppvi_min_altitude);
        paceCheckBox = view.findViewById(R.id.cb_pace);
        speedCheckBox = view.findViewById(R.id.cb_speed);
        paceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                speedCheckBox.setChecked(!b);
                if (b && trackData != null) {
                    isSpeed = false;
                    if (xAxis == 0) {
                        setPaceChartDataByTime(chartPoints);
                    } else if (xAxis == 1) {
                        setPaceChartDataByDistance(chartPoints);
                    }
                }
            }
        });
        speedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                paceCheckBox.setChecked(!b);
                if (b && trackData != null) {
                    isSpeed = true;
                    if (xAxis == 0) {
                        setSpeedChartDataByTime(chartPoints);
                    } else if (xAxis == 1) {
                        setSpeedChartDataByDistance(chartPoints);
                    }
                }
            }
        });
    }

    private void resetWidgets() {
        if (trackDetailViewModel.getUnit() == Constants.UNIT_IMPERIAL) {
            maxSpeedPpvi.setup(getString(R.string.max_speed_view_title),
                    getString(R.string.max_speed_view_unit_imperial),
                    getString(R.string.max_speed_view_default_value),
                    R.drawable.ic_max_speed);
            avgSpeedPpvi.setup(getString(R.string.avg_speed_view_title),
                    getString(R.string.avg_speed_view_unit_imperial),
                    getString(R.string.avg_speed_view_default_value),
                    R.drawable.ic_avg_speed);
            maxPacePpvi.setup(getString(R.string.max_pace_view_title),
                    getString(R.string.max_pace_view_unit_imperial),
                    getString(R.string.max_pace_view_default_value),
                    R.drawable.ic_max_speed);
            avgPacePpvi.setup(getString(R.string.avg_pace_view_title),
                    getString(R.string.avg_pace_view_unit_imperial),
                    getString(R.string.avg_pace_view_default_value),
                    R.drawable.ic_avg_speed);
            ascentPpvi.setup(getString(R.string.ascent_view_title),
                    getString(R.string.ascent_view_unit_imperial),
                    getString(R.string.ascent_view_default_value),
                    R.drawable.ic_ascent);
            descentPpvi.setup(getString(R.string.descent_view_title),
                    getString(R.string.descent_view_unit_imperial),
                    getString(R.string.descent_view_default_value),
                    R.drawable.ic_descent);
            maxAltitudePpvi.setup(getString(R.string.max_altitude_view_title),
                    getString(R.string.max_altitude_view_unit_imperial),
                    getString(R.string.max_altitude_view_default_value),
                    R.drawable.ic_max_altitude);
            minAltitudePpvi.setup(getString(R.string.min_altitude_view_title),
                    getString(R.string.min_altitude_view_unit_imperial),
                    getString(R.string.min_altitude_view_default_value),
                    R.drawable.ic_min_altitude);
        } else {
            maxSpeedPpvi.setup(getString(R.string.max_speed_view_title),
                    getString(R.string.max_speed_view_unit_imperial),
                    getString(R.string.max_speed_view_default_value),
                    R.drawable.ic_max_speed);
            avgSpeedPpvi.setup(getString(R.string.avg_speed_view_title),
                    getString(R.string.avg_speed_view_unit_imperial),
                    getString(R.string.avg_speed_view_default_value),
                    R.drawable.ic_avg_speed);
            maxPacePpvi.setup(getString(R.string.max_pace_view_title),
                    getString(R.string.max_pace_view_unit_imperial),
                    getString(R.string.max_pace_view_default_value),
                    R.drawable.ic_max_speed);
            avgPacePpvi.setup(getString(R.string.avg_pace_view_title),
                    getString(R.string.avg_pace_view_unit_imperial),
                    getString(R.string.avg_pace_view_default_value),
                    R.drawable.ic_avg_speed);
            ascentPpvi.setup(getString(R.string.ascent_view_title),
                    getString(R.string.ascent_view_unit_imperial),
                    getString(R.string.ascent_view_default_value),
                    R.drawable.ic_ascent);
            descentPpvi.setup(getString(R.string.descent_view_title),
                    getString(R.string.descent_view_unit_imperial),
                    getString(R.string.descent_view_default_value),
                    R.drawable.ic_descent);
            maxAltitudePpvi.setup(getString(R.string.max_altitude_view_title),
                    getString(R.string.max_altitude_view_unit_imperial),
                    getString(R.string.max_altitude_view_default_value),
                    R.drawable.ic_max_altitude);
            minAltitudePpvi.setup(getString(R.string.min_altitude_view_title),
                    getString(R.string.min_altitude_view_unit_imperial),
                    getString(R.string.min_altitude_view_default_value),
                    R.drawable.ic_min_altitude);
        }
        paceCheckBox.setChecked(false);
        speedCheckBox.setChecked(true);
    }

    private void setupCharts() {
        Description description = new Description();
        description.setText("");
        speedChart.setDescription(description);
        speedChart.getAxisRight().setEnabled(false);
        speedChart.setTouchEnabled(true);
        speedChart.setDragEnabled(true);
        speedChart.setScaleEnabled(true);
        speedChart.setPinchZoom(false);
        Legend l = speedChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        elevationChart.setDescription(description);
        elevationChart.getAxisRight().setEnabled(false);
        elevationChart.setTouchEnabled(false);
        elevationChart.setDragEnabled(false);
        elevationChart.setScaleEnabled(false);
        elevationChart.setPinchZoom(false);
        l = elevationChart.getLegend();

        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
    }

    @SuppressWarnings("ConstantConditions")
    private void observe() {
        trackDetailViewModel.getChartPoints().observe(this, new Observer<List<ChartPoint>>() {
            @Override
            public void onChanged(@Nullable final List<ChartPoint> chartPoints) {
                if (chartPoints != null) {
                    TrackDetailActivityChartsFragment.this.chartPoints = chartPoints;

                    setWidgetData(trackData);
                    setSpeedChartDataByDistance(chartPoints);
                    setElevationChartDataByDistance(chartPoints);
                }
            }
        });

        trackDetailViewModel.getTrackData().observe(this, new Observer<TrackData>() {
            @Override
            public void onChanged(@Nullable final TrackData trackData) {
                if (trackData != null) {
                    TrackDetailActivityChartsFragment.this.trackData = trackData;
                    setWidgetData(trackData);

                    editTitleImageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EditTitleDialog dialog = EditTitleDialog.newInstance(
                                    trackData.getTitle());
                            dialog.show(getActivity().getSupportFragmentManager(), null);
                        }
                    });
                }
            }
        });
    }

    private void setWidgetData(TrackData trackData) {
        if (trackData == null) {
            return;
        }

        String firstLetter = "";
        if (trackData.getTitle() != null && !trackData.getTitle().isEmpty()) {
            firstLetter = trackData.getTitle().substring(0, 1);
        }

        initialImageView.setImageDrawable(
                Utility.getInitial(firstLetter, String.valueOf(trackData.getStartTime()),
                        initialImageView.getLayoutParams().width));
        initialImageView.requestLayout();

        titleTextView.setText(trackData.getTitle());
        dateTextView.setText(StringUtils.getDateAsStringLocale(trackData.getStartTime()));

        maxSpeedPpvi.setValue(StringUtils.getSpeedAsThreeCharactersString(trackData.getMaxSpeed()));
        avgSpeedPpvi.setValue(StringUtils.getSpeedAsThreeCharactersString(trackData.getAvgSpeed()));
        double maxSpeed = trackData.getMaxSpeed();
        if (maxSpeed > 1) {
            maxPacePpvi.setValue(StringUtils.getPaceAsString(maxSpeed));
        } else {
            maxPacePpvi.setValue("-");
        }
        double avgSpeed = trackData.getAvgSpeed();
        if (avgSpeed > 1) {
            avgPacePpvi.setValue(StringUtils.getPaceAsString(avgSpeed));
        } else {
            avgPacePpvi.setValue("-");
        }
        ascentPpvi.setValue(String.format(Locale.getDefault(), "%.0f", trackData.getAscent()));
        descentPpvi.setValue(String.format(Locale.getDefault(), "%.0f", trackData.getDescent()));
        minAltitudePpvi.setValue(String.format(Locale.getDefault(), "%.0f", trackData.getMinAltitude()));
        maxAltitudePpvi.setValue(String.format(Locale.getDefault(), "%.0f", trackData.getMaxAltitude()));
    }

    private void setElevationChartDataByTime(List<ChartPoint> chartPoints) {
        List<Entry> values = new ArrayList<>();
        long startTime = chartPoints.get(0).getTime();
        long durationInSeconds = (chartPoints.get(chartPoints.size() - 1).getTime()
                - startTime)
                / 1000;

        for (ChartPoint chartPoint : chartPoints) {
            long elapsedSeconds = (chartPoint.getTime() - startTime) / 1000;
            values.add(new Entry((float) elapsedSeconds, (float) chartPoint.getAltitude()));
        }
        elevationChart.getXAxis().setValueFormatter(new GraphTimeAxisValueFormatter(durationInSeconds));
        LineDataSet elevationDataSet;
        if (trackDetailViewModel.getUnit() == Constants.UNIT_IMPERIAL) {
            elevationDataSet = new LineDataSet(values, getString(R.string.elevation_chart_title_imperial));
        } else {
            elevationDataSet = new LineDataSet(values, getString(R.string.elevation_chart_title));
        }
        elevationDataSet.setDrawHighlightIndicators(false);
        setElevationChartData(elevationDataSet);
    }

    private void setElevationChartDataByDistance(List<ChartPoint> chartPoints) {
        List<Entry> values = new ArrayList<>();

        for (ChartPoint chartPoint : chartPoints) {
            values.add(new Entry((float) chartPoint.getDistance(), (float) chartPoint.getAltitude()));
        }
        elevationChart.getXAxis().setValueFormatter(new LargeValueFormatter());
        LineDataSet elevationDataSet;
        if (trackDetailViewModel.getUnit() == Constants.UNIT_IMPERIAL) {
            elevationDataSet = new LineDataSet(values, getString(R.string.elevation_chart_title_imperial));
        } else {
            elevationDataSet = new LineDataSet(values, getString(R.string.elevation_chart_title));
        }
        elevationDataSet.setDrawHighlightIndicators(false);
        setElevationChartData(elevationDataSet);
    }

    private void setSpeedChartDataByTime(List<ChartPoint> chartPoints) {
        List<Entry> values = new ArrayList<>();

        long startTime = chartPoints.get(0).getTime();
        long durationInSeconds = (chartPoints.get(chartPoints.size() - 1).getTime()
                - startTime)
                / 1000;

        for (ChartPoint chartPoint : chartPoints) {
            long elapsedSeconds = (chartPoint.getTime() - startTime) / 1000;
            values.add(new Entry((float) elapsedSeconds, (float) chartPoint.getSpeed()));
        }
        speedChart.getXAxis().setValueFormatter(new GraphTimeAxisValueFormatter(durationInSeconds));

        LineDataSet speedDataSet;
        if (trackDetailViewModel.getUnit() == Constants.UNIT_IMPERIAL) {
            speedDataSet = new LineDataSet(values, getString(R.string.speed_chart_title_imperial));
        } else {
            speedDataSet = new LineDataSet(values, getString(R.string.speed_chart_title));
        }
        speedDataSet.setDrawHighlightIndicators(false);
        setSpeedChartData(speedDataSet);
    }

    private void setSpeedChartDataByDistance(List<ChartPoint> chartPoints) {
        List<Entry> values = new ArrayList<>();

        for (ChartPoint chartPoint : chartPoints) {
            values.add(new Entry((float) chartPoint.getDistance(), (float) chartPoint.getSpeed()));
        }
        speedChart.getXAxis().setValueFormatter(new LargeValueFormatter());
        LineDataSet speedDataSet;
        if (trackDetailViewModel.getUnit() == Constants.UNIT_IMPERIAL) {
            speedDataSet = new LineDataSet(values, getString(R.string.speed_chart_title_imperial));
        } else {
            speedDataSet = new LineDataSet(values, getString(R.string.speed_chart_title));
        }
        speedDataSet.setDrawHighlightIndicators(false);
        setSpeedChartData(speedDataSet);
    }

    private void setPaceChartDataByTime(List<ChartPoint> chartPoints) {
        List<Entry> values = new ArrayList<>();
        long startTime = chartPoints.get(0).getTime();
        long durationInSeconds = (chartPoints.get(chartPoints.size() - 1).getTime()
                - startTime)
                / 1000;
        double highestPaceValue = 0;

        for (ChartPoint chartPoint : chartPoints) {
            long elapsedSeconds = (chartPoint.getTime() - startTime) / 1000;
            if (chartPoint.getSpeed() > 1) {
                double pace = 60 * 60 * (1 / chartPoint.getSpeed());
                if (pace > highestPaceValue) {
                    highestPaceValue = pace;
                }
                values.add(new Entry((float) elapsedSeconds, (float) pace));
            } else {
                values.add(new Entry((float) elapsedSeconds, (float) 0));
            }
        }
        speedChart.getXAxis().setValueFormatter(new GraphTimeAxisValueFormatter(durationInSeconds));
        speedChart.getAxisLeft().setValueFormatter(new GraphTimeAxisValueFormatter((long) highestPaceValue));

        LineDataSet speedDataSet;
        if (trackDetailViewModel.getUnit() == Constants.UNIT_IMPERIAL) {
            speedDataSet = new LineDataSet(values, getString(R.string.pace_chart_title_imperial));
        } else {
            speedDataSet = new LineDataSet(values, getString(R.string.pace_chart_title));
        }
        speedDataSet.setDrawHighlightIndicators(false);
        setSpeedChartData(speedDataSet);
    }

    private void setPaceChartDataByDistance(List<ChartPoint> chartPoints) {
        List<Entry> values = new ArrayList<>();
        double highestPaceValue = 0;

        for (ChartPoint chartPoint : chartPoints) {
            if (chartPoint.getSpeed() > 1) {
                double pace = 60 * 60 * (1 / chartPoint.getSpeed());
                if (pace > highestPaceValue) {
                    highestPaceValue = pace;
                }
                values.add(new Entry((float) chartPoint.getDistance(), (float) pace));
            } else {
                values.add(new Entry((float) chartPoint.getDistance(), (float) 0));
            }
        }
        speedChart.getXAxis().setValueFormatter(new LargeValueFormatter());
        speedChart.getAxisLeft().setValueFormatter(new GraphTimeAxisValueFormatter((long) highestPaceValue));
        LineDataSet speedDataSet;
        if (trackDetailViewModel.getUnit() == Constants.UNIT_IMPERIAL) {
            speedDataSet = new LineDataSet(values, getString(R.string.pace_chart_title_imperial));
        } else {
            speedDataSet = new LineDataSet(values, getString(R.string.pace_chart_title));
        }
        speedDataSet.setDrawHighlightIndicators(false);
        setSpeedChartData(speedDataSet);
    }

    private void setElevationChartData(LineDataSet elevationDataSet) {
        elevationDataSet.setDrawIcons(false);
        elevationDataSet.setDrawValues(false);
        elevationDataSet.setColor(getResources().getColor(R.color.colorPrimary));
        elevationDataSet.setDrawCircles(false);
        elevationDataSet.setLineWidth(3f);
        elevationDataSet.setValueTextSize(9f);
        elevationDataSet.setDrawFilled(true);
        elevationDataSet.setFormLineWidth(1f);
        elevationDataSet.setFormSize(15.f);
        @SuppressWarnings("ConstantConditions")
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_primary_color);
        elevationDataSet.setFillDrawable(drawable);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(elevationDataSet);
        LineData data = new LineData(dataSets);
        elevationChart.setData(data);
        elevationChart.invalidate();
    }

    private void setSpeedChartData(LineDataSet speedDataSet) {
        speedDataSet.setDrawIcons(false);
        speedDataSet.setDrawValues(false);
        speedDataSet.setColor(getResources().getColor(R.color.colorAccent));
        speedDataSet.setDrawCircles(false);
        speedDataSet.setLineWidth(3f);
        speedDataSet.setValueTextSize(9f);
        speedDataSet.setDrawFilled(true);
        speedDataSet.setFormLineWidth(1f);
        speedDataSet.setFormSize(15.f);
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_accent_color);
        speedDataSet.setFillDrawable(drawable);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(speedDataSet);
        LineData data = new LineData(dataSets);
        speedChart.setData(data);
        speedChart.invalidate();
    }
}
