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
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.utils.Constants;
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.utils.StringUtils;
import com.awolity.trakr.utils.Utility;
import com.awolity.trakr.view.MapUtils;
import com.awolity.trakr.viewmodel.TrackViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class TrackDetailActivityMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = TrackDetailActivityMapFragment.class.getSimpleName();

    private GoogleMap googleMap;
    private MapView mapView;
    private TrackWithPoints trackWithPoints;
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
        //trackViewModel.init(trackId);
        trackViewModel.getTrackWithPoints()
                .observe(this, new Observer<TrackWithPoints>() {
                    @Override
                    public void onChanged(@Nullable final TrackWithPoints trackWithPoints) {
                        // MyLog.d(TAG, "onChanged");
                        if (trackWithPoints != null) {
                            TrackDetailActivityMapFragment.this.trackWithPoints = trackWithPoints;
                            showTrack();
                            setData(trackWithPoints);

                            editTitleImageButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    EditTitleDialog dialog = EditTitleDialog.newInstance(
                                            trackWithPoints.getTitle());
                                    dialog.show(getActivity().getSupportFragmentManager(), null);
                                }
                            });
                            getActivity().startPostponedEnterTransition();
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
        showTrack();
    }

    private void showTrack() {
        if (googleMap != null && trackWithPoints != null) {
            MapUtils.setupTrackPolyLine(getActivity(), googleMap, trackWithPoints, true);
            MapUtils.moveCameraToTrack(googleMap, trackWithPoints);
            LatLng start = trackWithPoints.getPointsLatLng().get(0);
            LatLng finish = trackWithPoints.getPointsLatLng().get(trackWithPoints.getPointsLatLng().size() - 1);
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

    private void setData(TrackWithPoints trackWithPoints) {
        // MyLog.d(TAG, "setData");

        String firstLetter = "";
        if (trackWithPoints.getTitle() != null && !trackWithPoints.getTitle().isEmpty()) {
            firstLetter = trackWithPoints.getTitle().substring(0, 1);
        }

        initialImageView.setImageDrawable(
                Utility.getInitial(firstLetter, String.valueOf(trackWithPoints.getStartTime()),
                        initialImageView.getLayoutParams().width));
        initialImageView.requestLayout();

        titleTextView.setText(trackWithPoints.getTitle());
        dateTextView.setText(StringUtils.getDateAsStringLocale(trackWithPoints.getStartTime()));
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
