package com.awolity.trakr.view.explore;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.awolity.trakr.BuildConfig;
import com.awolity.trakr.R;
import com.awolity.trakr.view.model.MapPoint;
import com.awolity.trakr.view.model.TrackData;
import com.awolity.trakr.sync.SyncService;
import com.awolity.trakr.view.detail.TrackDetailActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

public class ExploreActivity extends AppCompatActivity implements OnMapReadyCallback,
        TrackDetailsDialog.TrackDetailsDialogListener {

    private GoogleMap map;
    private TextView placeholderTv;
    private double np = 0, sp = 0, wp = 0, ep = 0;
    private ExploreViewModel exploreViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        placeholderTv = findViewById(R.id.tv_placeholder);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_explore));

        startService(new Intent(this, SyncService.class));

        setupMapView();
        setupAdview();
    }

    private void setupMapView() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //noinspection ConstantConditions
        mapFragment.getMapAsync(this);
    }

    @SuppressWarnings("SpellCheckingInspection")
    private void setupAdview() {
        AdView adview = findViewById(R.id.adView);
        if (BuildConfig.DEBUG) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("B20AC3BE6C392F942D699800329EDCBD")
                    .build();
            adview.loadAd(adRequest);
        } else {
            AdRequest adRequest = new AdRequest.Builder().build();
            adview.loadAd(adRequest);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnPolylineClickListener(polyline -> {
            TrackDetailsDialog dialog = new TrackDetailsDialog();
            dialog.setTrackData((TrackData) polyline.getTag());
            dialog.setUnit(exploreViewModel.getUnit());
            dialog.show(getSupportFragmentManager(), null);
        });
        setupViewmodel();
    }

    private void setupViewmodel() {
        exploreViewModel = ViewModelProviders.of(this)
                .get(ExploreViewModel.class);
        exploreViewModel.getTracksData().observe(this, tracksData -> {
            if (tracksData != null && tracksData.size() > 0) {
                for (TrackData trackData : tracksData) {
                    setBounds(trackData);

                    final Polyline polyline = setupPolyline(trackData,
                            getColor(String.valueOf(trackData.getStartTime())));

                    exploreViewModel.getMapPointsOfTrack(trackData.getTrackId())
                            .observe(ExploreActivity.this, mapPoints -> {
                                if (mapPoints != null) {
                                    polyline.setPoints(toLatLngList(mapPoints));
                                }
                            });
                }
            } else {
                placeholderTv.setVisibility(View.VISIBLE);
            }

        });
    }

    private void setBounds(TrackData track) {
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

        LatLngBounds bounds = new LatLngBounds(
                new LatLng(sp, wp),
                new LatLng(np, ep));
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private static int getColor(String s) {
        ColorGenerator generator = ColorGenerator.MATERIAL;
        return generator.getColor(s);
    }

    private Polyline setupPolyline(TrackData track, int color) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .geodesic(true)
                .color(color)
                .width(ExploreActivity.this.getResources().getInteger(R.integer.polyline_width))
                .zIndex(30)
                .visible(true);
        Polyline polyline = map.addPolyline(polylineOptions);
        polyline.setClickable(true);
        polyline.setTag(track);
        return polyline;
    }

    private List<LatLng> toLatLngList(List<MapPoint> mapPoints) {
        List<LatLng> latLngList = new ArrayList<>(mapPoints.size());
        for (MapPoint mapPoint : mapPoints) {
            latLngList.add(mapPoint.toLatLng());
        }
        return latLngList;
    }

    @Override
    public void onViewClicked(long id) {
        startActivity(TrackDetailActivity.getStarterIntent(ExploreActivity.this, id));
    }
}
