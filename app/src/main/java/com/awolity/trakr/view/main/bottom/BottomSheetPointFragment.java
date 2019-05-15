package com.awolity.trakr.view.main.bottom;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awolity.trakr.R;

import com.awolity.trakr.view.main.MainActivityViewModel;
import com.awolity.trakrviews.PrimaryPropertyView;
import com.awolity.trakrviews.SecondaryPropertyView;

import java.util.Locale;

public class BottomSheetPointFragment extends BottomSheetBaseFragment {

    private static final String TAG = BottomSheetPointFragment.class.getSimpleName();
    private MainActivityViewModel mainActivityViewModel;
    private PrimaryPropertyView speedView, altitudeView;
    private SecondaryPropertyView accuracyView, bearingView;

    public static BottomSheetPointFragment newInstance(String title) {
        BottomSheetPointFragment fragment = new BottomSheetPointFragment();
        fragment.setTitle(title);
        return fragment;
    }

    public BottomSheetPointFragment() {
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.activity_main_fragment_bottom_sheet_point, container, false);
        setupWidgets(view);

        setupViewModel();

        return view;
    }

    @SuppressWarnings("ConstantConditions")
    private void setupWidgets(View view) {
        speedView = view.findViewById(R.id.speedView);
        altitudeView = view.findViewById(R.id.altitudeView);
        accuracyView = view.findViewById(R.id.accuracyView);
        bearingView = view.findViewById(R.id.bearingView);

        speedView.setLabel(getActivity().getString(R.string.speed_view_label));
        speedView.setValue(getActivity().getString(R.string.speed_view_default_value));
        speedView.setUnit(getActivity().getString(R.string.speed_view_unit));

        altitudeView.setLabel(getActivity().getString(R.string.altitude_view_label));
        altitudeView.setValue(getActivity().getString(R.string.altitude_view_default_value));
        altitudeView.setUnit(getActivity().getString(R.string.altitude_view_unit));

        accuracyView.setLabel(getActivity().getString(R.string.accuracy_view_label));
        accuracyView.setValue(getActivity().getString(R.string.accuracy_view_default_value));
        accuracyView.setUnit(getActivity().getString(R.string.accuracy_view_unit));

        bearingView.setLabel(getActivity().getString(R.string.bearing_view_label));
        bearingView.setValue(getActivity().getString(R.string.bearing_view_default_value));
        bearingView.setUnit(getActivity().getString(R.string.bearing_view_unit));

    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void setupViewModel() {
        //noinspection ConstantConditions
        mainActivityViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
    }

    @SuppressWarnings("ConstantConditions")
    private void startLocationUpdates() {
        mainActivityViewModel.getLocation().observe(getActivity(), new Observer<Location>() {
            @Override
            public void onChanged(@Nullable Location location) {
                if (location != null) {
                    setData(location);
                }
            }
        });
    }

    private void stopLocationUpdates() {
        mainActivityViewModel.stopLocation();
    }

    private void setData(Location location) {
        setSpeed(location.getSpeed());
        setAltitude(location.getAltitude());
        setBearing(location.getBearing());
        setAccuracy(location.getAccuracy());
    }

    private void setSpeed(float speed) {
        String speedString = String.format(Locale.getDefault(), "%.1f", speed * 3.6f);
        speedView.setValue(speedString);
    }

    private void setAltitude(double altitude) {
        String altitudeString = String.format(Locale.getDefault(), "%.0f", altitude);
        altitudeView.setValue(altitudeString);
    }

    private void setAccuracy(float accuracy) {
        String accuracyString = String.format(Locale.getDefault(), "%.1f", accuracy);
        accuracyView.setValue(accuracyString);
    }

    private void setBearing(float bearing) {
        String bearingString = String.format(Locale.getDefault(), "%.1f", bearing);
        bearingView.setValue(bearingString);
    }
}