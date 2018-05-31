package com.awolity.trakr.view.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awolity.trakr.R;

import com.awolity.trakr.databinding.ActivityMainFragmentBottomSheetPointBinding;
import com.awolity.trakr.viewmodel.LocationViewModel;

import java.util.Locale;

public class BottomSheetPointFragment extends BottomSheetBaseFragment {

    private static final String LOG_TAG = BottomSheetPointFragment.class.getSimpleName();
    private ActivityMainFragmentBottomSheetPointBinding binding;
    private LocationViewModel locationViewModel;

    public static BottomSheetPointFragment newInstance(String title) {
        BottomSheetPointFragment fragment = new BottomSheetPointFragment();
        fragment.setTitle(title);
        return fragment;
    }

    public BottomSheetPointFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.activity_main_fragment_bottom_sheet_point, container, false);

        binding.speedView.setLabel(getActivity().getString(R.string.speed_view_label)); 
        binding.speedView.setValue(getActivity().getString(R.string.speed_view_value));
        binding.speedView.setUnit(getActivity().getString(R.string.speed_view_unit));

        binding.altitudeView.setLabel(getActivity().getString(R.string.altitude_view_label));
        binding.altitudeView.setValue(getActivity().getString(R.string.altitude_view_value));
        binding.altitudeView.setUnit(getActivity().getString(R.string.altitude_view_unit));

        binding.accuracyView.setLabel(getActivity().getString(R.string.accuracy_view_label)); 
        binding.accuracyView.setValue(getActivity().getString(R.string.accuracy_view_value));
        binding.accuracyView.setUnit(getActivity().getString(R.string.accuracy_view_unit));

        binding.bearingView.setLabel(getActivity().getString(R.string.bearing_view_label));
        binding.bearingView.setValue(getActivity().getString(R.string.bearing_view_value));
        binding.bearingView.setUnit(getActivity().getString(R.string.bearing_view_unit));

        setupViewModel();

        return binding.getRoot();
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

    private void setupViewModel(){
        //noinspection ConstantConditions
        locationViewModel = ViewModelProviders.of(getActivity()).get(LocationViewModel.class);
    }

    private void startLocationUpdates(){
        locationViewModel.getLocation().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(@Nullable Location location) {
                if(location!=null){
                    setData(location);
                }
            }
        });
    }

    private void stopLocationUpdates(){
        locationViewModel.stopLocation();
    }

    public void setData(Location location) {
        setSpeed(location.getSpeed());
        setAltitude(location.getAltitude());
        setBearing(location.getBearing());
        setAccuracy(location.getAccuracy());
    }

    private void setSpeed(float speed) {
        String speedString = String.format(Locale.getDefault(), "%.1f", speed);
        binding.speedView.setValue(speedString);
    }

    private void setAltitude(double altitude) {
        String altitudeString = String.format(Locale.getDefault(), "%.0f", altitude);
        binding.altitudeView.setValue(altitudeString);
    }

    private void setAccuracy(float accuracy) {
        String accuracyString = String.format(Locale.getDefault(), "%.1f", accuracy);
        binding.accuracyView.setValue(accuracyString);
    }

    private void setBearing(float bearing) {
        String bearingString = String.format(Locale.getDefault(), "%.1f", bearing);
        binding.bearingView.setValue(bearingString);
    }
}