package com.awolity.trakr.view.detail;

import com.awolity.trakr.data.entity.TrackpointEntity;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

public class GraphTimeAxisValueFormatter implements IAxisValueFormatter {

    private String[] mValues;

    public GraphTimeAxisValueFormatter(List<TrackpointEntity> trackPointEntities) {
        mValues = new String[trackPointEntities.size()];
        Duration duration;
        long startTime = trackPointEntities.get(0).getTime();
        long durationInSeconds = (trackPointEntities.get(trackPointEntities.size() - 1).getTime()
                - startTime)
                / 1000;
        if (durationInSeconds < 60) {// track shorter than a minute
            duration = GraphTimeAxisValueFormatter.Duration.Seconds;
        } else if (durationInSeconds < 3600) { //track shorter then an hour
            duration = GraphTimeAxisValueFormatter.Duration.Minutes;
        } else {
            duration = GraphTimeAxisValueFormatter.Duration.Hours;
        }

        for (int i = 0; i < trackPointEntities.size(); i++) {
            String s = "";
            long pointTime = (trackPointEntities.get(i).getTime() - startTime) / 1000;
            if (duration.equals(Duration.Seconds)) {
                s = String.valueOf(pointTime);
            } else if (duration.equals(Duration.Minutes)) {
                s = String.valueOf(pointTime / 60)
                        + ":"
                        + String.valueOf(pointTime % 60);
            } else if (duration.equals(Duration.Hours)) {
                s = String.valueOf(pointTime / 3600)
                        + ":"
                        + String.valueOf((pointTime % 3600) / 60)
                        + ":"
                        + String.valueOf(pointTime % 60);
            }
            mValues[i] = s;
        }
    }


    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if ( value >= 0) {
            return mValues[(int) value % mValues.length];
        } else
            return "";

    }

    public enum Duration {Seconds, Minutes, Hours,}
}
