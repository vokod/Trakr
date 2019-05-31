package com.awolity.trakr.view.detail;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.awolity.trakr.R;
import com.awolity.trakr.view.model.TrackData;
import com.awolity.trakr.view.MapUtils;
import com.awolity.trakr.view.model.MapPoint;
import com.awolity.trakr.utils.StringUtils;
import com.awolity.trakr.utils.Utility;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class TrackDetailActivityMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private MapView mapView;
    private TrackData trackData;
    private List<MapPoint> mapPoints;
    private TextView titleTextView, dateTextView;
    private ImageButton editTitleImageButton;
    private ImageView initialImageView;

    public TrackDetailActivityMapFragment() {
    }

    public static TrackDetailActivityMapFragment newInstance() {
        return new TrackDetailActivityMapFragment();
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_track_detail_fragment_map, container, false);
        setupWidgets(view, savedInstanceState);
        setupViewModel();
        return view;
    }

    private void setupWidgets(View view, Bundle savedInstanceState) {
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
        @SuppressWarnings("ConstantConditions") TrackDetailViewModel trackDetailViewModel = ViewModelProviders.of(getActivity())
                .get(TrackDetailViewModel.class);

        trackDetailViewModel.getMapPoints().observe(this, mapPoints -> {
            if (mapPoints != null) {
                TrackDetailActivityMapFragment.this.mapPoints = mapPoints;
                setTrack();

                editTitleImageButton.setOnClickListener(view -> {
                    EditTitleDialog dialog = EditTitleDialog.newInstance(
                            trackData.getTitle());
                    dialog.show(getActivity().getSupportFragmentManager(), null);
                });
                //noinspection ConstantConditions
                getActivity().startPostponedEnterTransition();
            }
        });

        trackDetailViewModel.getTrackData().observe(this, trackEntity -> {
            if (trackEntity != null) {
                TrackDetailActivityMapFragment.this.trackData = trackEntity;
                setData(trackEntity);
                setTrack();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        setTrack();
    }

    private void setTrack() {
        if (googleMap != null && trackData != null && mapPoints != null) {
            MapUtils.setupTrackPolyLine(getActivity(), googleMap, mapPoints);
            MapUtils.moveCameraToTrack(googleMap, trackData);
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

    private void setData(TrackData trackData) {
        // MyLog.d(TAG, "setData");

        String firstLetter = "";
        if (trackData.getTitle() != null && !trackData.getTitle().isEmpty()) {
            firstLetter = trackData.getTitle().substring(0, 1);
        }

        initialImageView.setImageDrawable(
                Utility.getInitial(firstLetter, String.valueOf(trackData.getStartTime()),
                        initialImageView.getLayoutParams().width));
        initialImageView.requestLayout();

        titleTextView.setText(trackData.getTitle());
        dateTextView.setText(StringUtils.getDateAsStringLocale(trackData.getStartTime()));
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
