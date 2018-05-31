package com.awolity.trakr.view.main;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.awolity.trakr.utils.MyLog;

public class BottomSheetFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = BottomSheetFragmentPagerAdapter.class.getSimpleName();
    private BottomSheetBaseFragment[] fragments;

    BottomSheetFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    void setFragments(BottomSheetBaseFragment[] fragments){
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