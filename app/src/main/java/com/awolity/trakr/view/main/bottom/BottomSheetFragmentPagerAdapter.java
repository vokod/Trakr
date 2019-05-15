package com.awolity.trakr.view.main.bottom;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class BottomSheetFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = BottomSheetFragmentPagerAdapter.class.getSimpleName();
    private BottomSheetBaseFragment[] fragments;

    public BottomSheetFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setFragments(BottomSheetBaseFragment[] fragments){
       // MyLog.d(TAG, "setFragments");
        this.fragments = fragments;
    }

    @Override
    public BottomSheetBaseFragment getItem(int position) {
       // MyLog.d(TAG,"getItem: " + position);
        if(fragments!=null) {
            return fragments[position];
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        if(fragments!=null) {
            return fragments.length;
        } else {
            return 0;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
      // MyLog.d(TAG,"getPageTitle: " + position);
       return fragments[position].getTitle();
    }
}