package com.awolity.trakr.view.explore;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.sync.SyncService;
import com.awolity.trakr.view.detail.TrackDetailActivity;
import com.awolity.trakr.viewmodel.TrackListViewModel;
import com.awolity.trakrutils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class ExploreActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private TrackListViewModel trackListViewModel;
    private TextView placeholderTv;
    private double np = 0, sp = 0, wp = 0, ep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        placeholderTv = findViewById(R.id.tv_placeholder);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_explore));

        // start syncing
        startService(new Intent(this, SyncService.class));

        setupMapView();

        trackListViewModel = ViewModelProviders.of(this).get(TrackListViewModel.class);
    }

    private void setupMapView() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                startActivity(TrackDetailActivity.getStarterIntent(ExploreActivity.this,
                        (long) polyline.getTag()));
            }
        });
        setupPolyLines();
    }

    private void setupPolyLines() {
        trackListViewModel.getSimplifiedTracksWithPoints(
                Constants.SIMPLIFIED_TRACK_POINT_MAX_NUMBER_FOR_DETAILS)
                .observe(this, new Observer<List<TrackWithPoints>>() {
                    @Override
                    public void onChanged(@Nullable List<TrackWithPoints> tracksWithPoints) {
                        // TODO: ezt az egészet egy bacground threadbe
                        if (tracksWithPoints != null && tracksWithPoints.size() > 0) {
                            placeholderTv.setVisibility(View.GONE);
                            for (TrackWithPoints trackWithPoints : tracksWithPoints) {
                                setBounds(trackWithPoints.getTrackEntity());
                                Polyline polyline = setupPolyline(trackWithPoints.getTrackId(),
                                        getColor(String.valueOf(trackWithPoints.getStartTime())));
                                polyline.setPoints(getPoints(trackWithPoints));
                            }

                            LatLngBounds bounds = new LatLngBounds(
                                    new LatLng(sp, wp),
                                    new LatLng(np, ep));
                            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

                        } else {
                            placeholderTv.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void setBounds(TrackEntity track) {
        if (track.getNorthestPoint() > np || np == 0) {
            np = track.getNorthestPoint();
        }
        if (sp > track.getSouthestPoint() || sp == 0) {
            sp = track.getSouthestPoint();
        }
        if (track.getWesternPoint() < wp || wp == 0) {
            wp = track.getWesternPoint();
        }
        if (track.getEasternPoint() > ep || ep == 0) {
            ep = track.getEasternPoint();
        }
    }

    private static int getColor(String s) {
        ColorGenerator generator = ColorGenerator.MATERIAL;
        return generator.getColor(s);
    }

    private Polyline setupPolyline(long id, int color) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .geodesic(true)
                .color(color)
                .width(ExploreActivity.this.getResources().getInteger(R.integer.polyline_width))
                .zIndex(30)
                .visible(true);
        Polyline polyline = map.addPolyline(polylineOptions);
        polyline.setClickable(true);
        polyline.setTag(id);
        return polyline;
    }

    private List<LatLng> getPoints(TrackWithPoints trackWithPoints){
        List<LatLng> latLngList = new ArrayList<>(trackWithPoints.getTrackPoints().size());
        for (TrackpointEntity trackpointEntity : trackWithPoints.getTrackPoints()) {
            latLngList.add(new LatLng(trackpointEntity.getLatitude(), trackpointEntity.getLongitude()));
        }
        return latLngList;
    }
}
