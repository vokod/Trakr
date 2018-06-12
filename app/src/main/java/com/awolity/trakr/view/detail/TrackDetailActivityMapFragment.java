package com.awolity.trakr.view.detail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.view.MapUtils;
import com.awolity.trakr.viewmodel.TrackViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TrackDetailActivityMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_TRACK_ID = "track_id";
    private static final String LOG_TAG = TrackDetailActivityMapFragment.class.getSimpleName();

    private GoogleMap googleMap;
    private MapView mapView;
    private TrackWithPoints trackWithPoints;

    public TrackDetailActivityMapFragment() {
    }

    public static TrackDetailActivityMapFragment newInstance(long trackId) {
        TrackDetailActivityMapFragment fragment = new TrackDetailActivityMapFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TRACK_ID, trackId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // MyLog.d(LOG_TAG, "onCreate - " + this.hashCode());
        if (getArguments() != null) {
            long trackId = getArguments().getLong(ARG_TRACK_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // MyLog.d(LOG_TAG, "onCreateView " + this.hashCode());
        View view = inflater.inflate(R.layout.activity_track_detail_fragment_map, container, false);
        setupMapView(view, savedInstanceState);
        setupViewModel();
        return view;
    }

    private void setupMapView(View view, Bundle savedInstanceState) {
        // MyLog.d(LOG_TAG, "setupMapView");
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    private void setupViewModel() {
        // MyLog.d(LOG_TAG, "setupViewModel");
        TrackViewModel trackViewModel = ViewModelProviders.of(getActivity()).get(TrackViewModel.class);
        //trackViewModel.init(trackId);
        trackViewModel.getTrackWithPoints().observe(this, new Observer<TrackWithPoints>() {
            @Override
            public void onChanged(@Nullable TrackWithPoints trackWithPoints) {
                // MyLog.d(LOG_TAG, "onChanged");
                if (trackWithPoints != null) {
                    TrackDetailActivityMapFragment.this.trackWithPoints = trackWithPoints;
                    showTrack();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // MyLog.d(LOG_TAG, "onMapReady");
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        showTrack();
    }

    private void showTrack() {
        if (googleMap != null && trackWithPoints != null) {
            MapUtils.setupTrackPolyLine(getActivity(), googleMap, trackWithPoints, true);
            MapUtils.moveCameraToTrack(googleMap, trackWithPoints);
            LatLng start = trackWithPoints.getPointsLatLng().get(0);
            LatLng finish = trackWithPoints.getPointsLatLng().get(trackWithPoints.getPointsLatLng().size() - 1);
            // TODO
            googleMap.addMarker(new MarkerOptions()
                    .position(start)
                    .title("Start")
                    // TODO: rendes markereket
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            googleMap.addMarker(new MarkerOptions()
                    .position(finish)
                    .title("Finish")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
