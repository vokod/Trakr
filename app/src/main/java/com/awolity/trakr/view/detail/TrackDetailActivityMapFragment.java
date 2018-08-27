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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.view.MapUtils;
import com.awolity.trakr.viewmodel.TrackViewModel;
import com.awolity.trakr.viewmodel.model.MapPoint;
import com.awolity.trakrutils.StringUtils;
import com.awolity.trakrutils.Utility;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class TrackDetailActivityMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = TrackDetailActivityMapFragment.class.getSimpleName();

    private GoogleMap googleMap;
    private MapView mapView;
    private TrackEntity trackEntity;
    private List<MapPoint> mapPoints;
    private TextView titleTextView, dateTextView;
    private ImageButton editTitleImageButton;
    private ImageView initialImageView;

    public TrackDetailActivityMapFragment() {
    }

    public static TrackDetailActivityMapFragment newInstance() {
        return new TrackDetailActivityMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // MyLog.d(TAG, "onCreate - " + this.hashCode());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // MyLog.d(TAG, "onCreateView " + this.hashCode());
        View view = inflater.inflate(R.layout.activity_track_detail_fragment_map, container, false);
        setupWidgets(view, savedInstanceState);
        setupViewModel();
        return view;
    }

    private void setupWidgets(View view, Bundle savedInstanceState) {
        // MyLog.d(TAG, "setupMapView");
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        initialImageView = view.findViewById(R.id.iv_icon);
        editTitleImageButton = view.findViewById(R.id.ib_edit);
        titleTextView = view.findViewById(R.id.tv_title);
        dateTextView = view.findViewById(R.id.tv_date);
    }

    private void setupViewModel() {
        // MyLog.d(TAG, "setupViewModel");
        TrackViewModel trackViewModel = ViewModelProviders.of(getActivity()).get(TrackViewModel.class);

        trackViewModel.getMapPoints().observe(this, new Observer<List<MapPoint>>() {
            @Override
            public void onChanged(@Nullable List<MapPoint> mapPoints) {
                if (mapPoints != null) {
                    TrackDetailActivityMapFragment.this.mapPoints = mapPoints;
                    setTrack();

                    editTitleImageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EditTitleDialog dialog = EditTitleDialog.newInstance(
                                    trackEntity.getTitle());
                            dialog.show(getActivity().getSupportFragmentManager(), null);
                        }
                    });
                    getActivity().startPostponedEnterTransition();
                }
            }
        });

        trackViewModel.getTrack().observe(this, new Observer<TrackEntity>() {
            @Override
            public void onChanged(@Nullable TrackEntity trackEntity) {
                if (trackEntity != null) {
                    TrackDetailActivityMapFragment.this.trackEntity = trackEntity;
                    setData(trackEntity);
                    setTrack();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // MyLog.d(TAG, "onMapReady");
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        setTrack();
    }

    private void setTrack() {
        if (googleMap != null && trackEntity != null && mapPoints != null) {
            MapUtils.setupTrackPolyLine(getActivity(), googleMap, mapPoints);
            MapUtils.moveCameraToTrack(googleMap, trackEntity);
            LatLng start = mapPoints.get(0).toLatLng();
            LatLng finish = mapPoints.get(mapPoints.size() - 1).toLatLng();
            googleMap.addMarker(new MarkerOptions()
                    .position(start)
                    .title(getString(R.string.start))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            googleMap.addMarker(new MarkerOptions()
                    .position(finish)
                    .title(getString(R.string.finish))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
    }

    private void setData(TrackEntity trackEntity) {
        // MyLog.d(TAG, "setData");

        String firstLetter = "";
        if (trackEntity.getTitle() != null && !trackEntity.getTitle().isEmpty()) {
            firstLetter = trackEntity.getTitle().substring(0, 1);
        }

        initialImageView.setImageDrawable(
                Utility.getInitial(firstLetter, String.valueOf(trackEntity.getStartTime()),
                        initialImageView.getLayoutParams().width));
        initialImageView.requestLayout();

        titleTextView.setText(trackEntity.getTitle());
        dateTextView.setText(StringUtils.getDateAsStringLocale(trackEntity.getStartTime()));
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
