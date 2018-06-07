package com.awolity.trakr.activitytype;

import com.amulyakhare.textdrawable.util.ColorGenerator;

public class ActivityType {

    private RecordParameters recordParameters;
    private String title;
    private int iconResource;
    private int color;

    public ActivityType(String title, int iconResource, RecordParameters recordParameters){
        this.title = title;
        this.iconResource = iconResource;
        this.recordParameters = recordParameters;

        ColorGenerator generator = ColorGenerator.MATERIAL;
        color = generator.getColor(title);
    }

    public RecordParameters getRecordParameters() {
        return recordParameters;
    }

    public String getTitle() {
        return title;
    }

    public int getIconResource() {
        return iconResource;
    }

    public int getColor() {
        return color;
    }
}
