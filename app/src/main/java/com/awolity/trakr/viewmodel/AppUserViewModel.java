package com.awolity.trakr.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.awolity.trakr.di.TrakrApplication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AppUserViewModel extends ViewModel {

    private static final String LOG_TAG = AppUserViewModel.class.getSimpleName();

    public AppUserViewModel() {
    }

    public FirebaseUser getAppUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public boolean IsAppUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }


}
