package com.awolity.trakr.view.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.repository.AppUserRepository;
import com.awolity.trakr.repository.SettingsRepository;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

public class SettingsViewModel extends ViewModel {

    @SuppressWarnings("WeakerAccess")
    @Inject
    SettingsRepository settingsRepository;

    @SuppressWarnings("WeakerAccess")
    @Inject
    AppUserRepository appUserRepository;

    private final MutableLiveData<Boolean> isAppUserLoggedIn;

    public SettingsViewModel() {
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

    public int getAccuracy() {
        return settingsRepository.getAccuracy();
    }

    public void setAccuracy(int accuracy) {
        settingsRepository.setAccuracy(accuracy);
    }

    public int getUnit() {
        return settingsRepository.getUnit();
    }

    public void setUnit(int unit) {
        settingsRepository.setUnit(unit);
    }
}
