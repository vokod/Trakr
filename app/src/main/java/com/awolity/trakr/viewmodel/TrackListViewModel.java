package com.awolity.trakr.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.di.TrakrApplication;
import com.awolity.trakr.repository.Repository;

import java.util.List;

import javax.inject.Inject;

public class TrackListViewModel extends ViewModel{

    @SuppressWarnings("WeakerAccess")
    @Inject
    Repository repository;

    private LiveData<List<TrackEntity>> tracks;

    private static final String TAG = TrackListViewModel.class.getSimpleName();

    public TrackListViewModel() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }


    public LiveData<List<TrackEntity>> getTracks() {
        if (tracks == null) {
            tracks = repository.getTracks();
        }
        return tracks;
    }
}
