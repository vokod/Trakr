package com.awolity.trakr.activitytype;

public class ActivityType {

    private RecordParameters recordParameters;
    private String title;
    private int iconResource;
    private int menuIconResource;
   /* private int color;
    private String hexColor;*/

    public ActivityType(String title, int iconResource, int menuIconResource, RecordParameters recordParameters){
        this.title = title;
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

    public int getMenuIconResource(){
        return menuIconResource;
    }

    /*public int getColor() {
        return color;
    }*/
}
