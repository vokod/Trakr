package com.awolity.trakr.activitytype;

public class ActivityType {

    private final RecordParameters recordParameters;
    private final String title;
    private final String key;
    private final int iconResource;
    private final int menuIconResource;
   /* private int color;
    private String hexColor;*/

    public ActivityType(String title, String key, int iconResource, int menuIconResource, RecordParameters recordParameters) {
        this.title = title;
        this.key = key;
        this.iconResource = iconResource;
        this.recordParameters = recordParameters;
        this.menuIconResource = menuIconResource;

       /* ColorGenerator generator = ColorGenerator.MATERIAL;
        color = generator.getColor(title);
        hexColor = String.format("#%06X", (0xFFFFFF & color));*/
    }

  /*  public Drawable getBackgroundDrawable(){
        return new ColorDrawable(color);
    }*/

    public RecordParameters getRecordParameters() {
        return recordParameters;
    }

    public String getTitle() {
        return title;
    }

    public int getIconResource() {
        return iconResource;
    }

    public int getMenuIconResource() {
        return menuIconResource;
    }

    public String getKey() {
        return key;
    }

    /*public int getColor() {
        return color;
    }*/
}
