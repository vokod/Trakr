package com.awolity.trakr.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.repository.AppUserRepository;
import com.awolity.trakr.repository.TrackRepository;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

public class AppUserViewModel extends ViewModel {

    private static final String TAG = AppUserViewModel.class.getSimpleName();

    @Inject
    AppUserRepository appUserRepository;

    public AppUserViewModel() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    public FirebaseUser getAppUser() {
        return appUserRepository.getAppUser();
    }

    public boolean IsAppUserLoggedIn() {
        return appUserRepository.IsAppUserLoggedIn();
    }

    public void signOut(){
        appUserRepository.signOut();
    }

    public void deleteAccount(){
        appUserRepository.deleteUser();
    }

    public void signIn(){
        appUserRepository.signIn();
    }

}
