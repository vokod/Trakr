package com.awolity.trakr.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.repository.AppUserRepository;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

public class AppUserViewModel extends ViewModel {

    private static final String TAG = AppUserViewModel.class.getSimpleName();
    private final MutableLiveData<Boolean> isAppUserLoggedIn;

    @SuppressWarnings("WeakerAccess")
    @Inject
    AppUserRepository appUserRepository;

    public AppUserViewModel() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
        isAppUserLoggedIn = new MutableLiveData<>();
        isAppUserLoggedIn.postValue(appUserRepository.IsAppUserLoggedIn());

        appUserRepository.setAppUserStatusListener(new AppUserRepository.AppUserStatusListener() {
            @Override
            public void onSignOut() {
                isAppUserLoggedIn.postValue(false);
            }

            @Override
            public void onSignIn() {
                isAppUserLoggedIn.postValue(true);
            }

            @Override
            public void onDeleteAccount() {
                isAppUserLoggedIn.postValue(false);
            }
        });
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

    public LiveData<Boolean> getIsAppUserLoggedIn() {
        return isAppUserLoggedIn;
    }

}
