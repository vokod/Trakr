package com.awolity.trakr.model;

import com.awolity.trakr.data.entity.TrackpointEntity;

public class TrackPointMapPointConverter {

    private  TrackPointMapPointConverter(){}

    public static MapPoint toMapPoint(TrackpointEntity trackpoint){
        MapPoint result = new MapPoint();
        result.setLatitude(trackpoint.getLatitude());
        result.setLongitude(trackpoint.getLongitude());
        return result;
    }

}
