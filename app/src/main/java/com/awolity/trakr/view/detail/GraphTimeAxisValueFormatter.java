package com.awolity.trakr.view.detail;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class GraphTimeAxisValueFormatter implements IAxisValueFormatter {

    private final String[] values;

    GraphTimeAxisValueFormatter(long durationInSeconds) {
        Duration duration;
        values = new String[(int) durationInSeconds];

        if (durationInSeconds < 60) {// track shorter than a minute
            duration = GraphTimeAxisValueFormatter.Duration.Seconds;
        } else if (durationInSeconds < 3600) { //track shorter then an hour
            duration = GraphTimeAxisValueFormatter.Duration.Minutes;
        } else {
            duration = GraphTimeAxisValueFormatter.Duration.Hours;
        }

        for (int i = 0; i < durationInSeconds; i++) {
            String s;
            if (duration.equals(Duration.Seconds)) {
                s = String.valueOf(i);
            } else if (duration.equals(Duration.Minutes)) {
                s = i / 60
                        + ":"
                        + (i % 60);
            } else {
                s = i / 3600
                        + ":"
                        + ((i % 3600) / 60)
                        + ":"
                        + (i % 60);
            }
            values[i] = s;
        }
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // MyLog.d(TAG, "getFormattedValue - value: " + value);
        if (value >= 0) {
            return values[(int) value % values.length];
        } else
            return "";

    }

    public enum Duration {Seconds, Minutes, Hours,}
}
