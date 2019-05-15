package com.awolity.trakr.view.main.bottom;

import androidx.fragment.app.Fragment;

public abstract class BottomSheetBaseFragment extends Fragment {

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
