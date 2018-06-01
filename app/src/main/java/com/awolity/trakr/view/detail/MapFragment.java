package com.awolity.trakr.view.detail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.databinding.FragmentMapBinding;
import com.awolity.trakr.viewmodel.TrackViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_TRACK_ID = "track_id";

    private long trackId;
    private TrackViewModel trackViewModel;
    private GoogleMap googleMap;
    private FragmentMapBinding binding;

    public MapFragment() {
    }

    public static MapFragment newInstance(long trackId) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TRACK_ID, trackId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            trackId = getArguments().getLong(ARG_TRACK_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
        View view = binding.getRoot();

        setupMapView();
        return view;
    }

    private void setupMapView() {
        /* if (checkPlayServices()) {*/
        binding.mapView.getMapAsync(this);
    }

    private void setupViewModel() {
        trackViewModel = ViewModelProviders.of(this).get(TrackViewModel.class);
        trackViewModel.init(trackId);
        trackViewModel.getTrackWithPoints().observe(this, new Observer<TrackWithPoints>() {
            @Override
            public void onChanged(@Nullable TrackWithPoints trackWithPoints) {
                if (trackWithPoints != null && googleMap != null) {
                    LatLngBounds latLngBounds = new LatLngBounds(
                            new LatLng(trackWithPoints.getSouthestPoint(), trackWithPoints.getWesternPoint()),
                            new LatLng(trackWithPoints.getNorthestPoint(), trackWithPoints.getEasternPoint()));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            // MyLog.e(TAG, e.getLocalizedMessage());
        }

        setupViewModel();
    }
}
