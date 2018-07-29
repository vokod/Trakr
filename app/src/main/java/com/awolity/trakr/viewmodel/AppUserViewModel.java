package com.awolity.trakr.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AppUserViewModel extends ViewModel {

    private static final String TAG = AppUserViewModel.class.getSimpleName();

    public AppUserViewModel() {
    }

    public FirebaseUser getAppUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public boolean IsAppUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public void signOut(){
        FirebaseAuth.getInstance().signOut();
    }

}
