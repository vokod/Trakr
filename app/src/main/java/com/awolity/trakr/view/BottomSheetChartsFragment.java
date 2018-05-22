package com.awolity.trakr.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awolity.trakr.R;

public class BottomSheetChartsFragment extends BottomSheetBaseFragment {

    public static BottomSheetChartsFragment newInstance(String title){
        BottomSheetChartsFragment fragment = new BottomSheetChartsFragment();
        fragment.setTitle(title);
        return fragment;
    }

    public BottomSheetChartsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_main_fragment_bottom_sheet_diagram, container, false);
    }

}
