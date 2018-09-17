package com.awolity.trakr.view.map;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.view.detail.TrackDetailActivity;
import com.awolity.trakr.view.main.MainActivity;
import com.awolity.trakr.viewmodel.TrackListViewModel;
import com.awolity.trakrutils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private TrackListViewModel trackListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_map));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        trackListViewModel = ViewModelProviders.of(this).get(TrackListViewModel.class);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                startActivity(TrackDetailActivity.getStarterIntent(MapActivity.this,
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
                            double np = 0, sp = 0, wp = 0, ep = 0;

                            for (TrackWithPoints trackWithPoints : tracksWithPoints) {

                                if (trackWithPoints.getNorthestPoint() > np || np == 0) {
                                    np = trackWithPoints.getNorthestPoint();
                                }
                                if (sp > trackWithPoints.getSouthestPoint() || sp == 0) {
                                    sp = trackWithPoints.getSouthestPoint();
                                }
                                if (trackWithPoints.getWesternPoint() < wp || wp == 0) {
                                    wp = trackWithPoints.getWesternPoint();
                                }
                                if (trackWithPoints.getEasternPoint() > ep || ep == 0) {
                                    ep = trackWithPoints.getEasternPoint();
                                }

                                ColorGenerator generator = ColorGenerator.MATERIAL;
                                int color = generator.getColor(String.valueOf(trackWithPoints.getStartTime()));

                                PolylineOptions polylineOptions = new PolylineOptions()
                                        .geodesic(true)
                                        .color(color)
                                        .width(MapActivity.this.getResources().getInteger(R.integer.polyline_width))
                                        .zIndex(30)
                                        .visible(true);
                                Polyline polyline = map.addPolyline(polylineOptions);
                                polyline.setClickable(true);
                                polyline.setTag(trackWithPoints.getTrackId());

                                List<LatLng> latLngList = new ArrayList<>(trackWithPoints.getTrackPoints().size());
                                for (TrackpointEntity trackpointEntity : trackWithPoints.getTrackPoints()) {
                                    latLngList.add(new LatLng(trackpointEntity.getLatitude(), trackpointEntity.getLongitude()));
                                }
                                polyline.setPoints(latLngList);
                            }

                            LatLngBounds bounds = new LatLngBounds(
                                    new LatLng(sp, wp),
                                    new LatLng(np, ep));
                            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

                        }
                    }
                });


    }
}
