package com.awolity.trakr.gpx;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.notification.NotificationUtils;
import com.awolity.trakr.utils.IOUtils;
import com.awolity.trakr.utils.MyLog;

import com.codebutchery.androidgpx.data.GPXDocument;
import com.codebutchery.androidgpx.data.GPXRoute;
import com.codebutchery.androidgpx.data.GPXSegment;
import com.codebutchery.androidgpx.data.GPXTrack;
import com.codebutchery.androidgpx.data.GPXTrackPoint;
import com.codebutchery.androidgpx.data.GPXWayPoint;
import com.codebutchery.androidgpx.print.GPXFilePrinter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GpxExporter {

    private static final String LOG_TAG = GpxExporter.class.getSimpleName();
    private static final String PATH = Environment.getExternalStorageDirectory() + "/Trakr/";

    public static void export(final Context context, final TrackWithPoints trackWithPoints) {

        if (!IOUtils.isExternalStorageWritable()) {
            Toast.makeText(context,
                    context.getString(R.string.toast_external_storage_not_writable),
                    Toast.LENGTH_LONG).show();
            return;
        }
        IOUtils.checkCreateFolder(PATH.substring(0, PATH.length() - 1));

        GPXSegment gpxTrackSegment = new GPXSegment();

        for (TrackpointEntity trackpointEntity : trackWithPoints.getTrackPoints()) {
            GPXTrackPoint gpxTrackPoint = new GPXTrackPoint(
                    (float) trackpointEntity.getLatitude(),
                    (float) trackpointEntity.getLongitude());
            gpxTrackPoint.setElevation((float) trackpointEntity.getAltitude());
            gpxTrackPoint.setTimeStamp(new Date(trackpointEntity.getTime()));

            gpxTrackSegment.addPoint(gpxTrackPoint);
        }

        GPXTrack gpxTrack = new GPXTrack();
        gpxTrack.addSegment(gpxTrackSegment);
        gpxTrack.setName(trackWithPoints.getTitle());
        gpxTrack.setUserDescription(trackWithPoints.getMetadata());

        List<GPXTrack> gpxTracks = new ArrayList<>(1);
        gpxTracks.add(gpxTrack);
        List<GPXWayPoint> gpxWayPoints = new ArrayList<>(1);
        List<GPXRoute> gpxRoutes = new ArrayList<>(1);

        GPXDocument gpxDocument = new GPXDocument(gpxWayPoints, gpxTracks, gpxRoutes);

        final String fileName = IOUtils.getLegalizedFilename(trackWithPoints.getTitle() + ".gpx");
        GPXFilePrinter printer = new GPXFilePrinter(new GPXFilePrinter.GPXFilePrinterListener() {
            @Override
            public void onGPXPrintStarted() {
                MyLog.d(LOG_TAG, "onGPXPrintStarted");
                NotificationUtils.showExportTrackNotification(context, trackWithPoints.getTrackId(), fileName, PATH);
            }

            @Override
            public void onGPXPrintCompleted() {
                MyLog.d(LOG_TAG, "onGPXPrintCompleted");
                NotificationUtils.showExportTrackDoneNotification(context, fileName, PATH);
            }

            @Override
            public void onGPXPrintError(String message) {
                MyLog.d(LOG_TAG, "onGPXPrintError: " + message);
                NotificationUtils.showExportTrackErrorNotification(context, trackWithPoints.getTrackId(), fileName, PATH);

            }
        });
        printer.print(gpxDocument, PATH + fileName);
    }

}
