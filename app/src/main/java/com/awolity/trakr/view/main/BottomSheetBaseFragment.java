package com.awolity.trakr.view.main;

import android.support.v4.app.Fragment;

public abstract class BottomSheetBaseFragment extends Fragment {

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
