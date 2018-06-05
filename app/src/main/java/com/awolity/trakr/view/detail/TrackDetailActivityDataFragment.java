package com.awolity.trakr.view.detail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awolity.trakr.R;
import com.awolity.trakr.viewmodel.TrackViewModel;


public class TrackDetailActivityDataFragment extends Fragment {


    private static final String ARG_TRACK_ID = "track_id";
    private static final String LOG_TAG = TrackDetailActivityDataFragment.class.getSimpleName();

    private long trackId;
    private TrackViewModel trackViewModel;

    public static TrackDetailActivityDataFragment newInstance(long trackId) {
        TrackDetailActivityDataFragment fragment = new TrackDetailActivityDataFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TRACK_ID, trackId);
        fragment.setArguments(args);
        return fragment;
    }

    public TrackDetailActivityDataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_track_detail_fragment_data, container, false);
    }

}
