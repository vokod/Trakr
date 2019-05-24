package com.awolity.trakr.view.settings;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.repository.AppUserRepository;
import com.awolity.trakr.repository.SettingsRepository;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

public class SettingsViewModel extends ViewModel {

    @Inject
    SettingsRepository settingsRepository;

    @Inject
    AppUserRepository appUserRepository;

    @Inject
    Context context;

    private final MutableLiveData<Boolean> isAppUserLoggedIn;
    private final FirebaseAnalytics firebaseAnalytics;

    public SettingsViewModel() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        isAppUserLoggedIn = new MutableLiveData<>();
        isAppUserLoggedIn.postValue(appUserRepository.IsAppUserLoggedIn());

        appUserRepository.setAppUserStatusListener(new AppUserRepository.AppUserStatusListener() {
            @Override
            public void onSignOut() {
                isAppUserLoggedIn.postValue(false);
            }

            @Override
            public void onSignIn() {
                logLoginEvent();
                isAppUserLoggedIn.postValue(true);
            }

            @Override
            public void onDeleteAccount() {
                isAppUserLoggedIn.postValue(false);
            }
        });
    }

    FirebaseUser getAppUser() {
        return appUserRepository.getAppUser();
    }

    boolean IsAppUserLoggedIn() {
        return appUserRepository.IsAppUserLoggedIn();
    }

    void signOut() {
        appUserRepository.signOut();
    }

    void deleteAccount() {
        appUserRepository.deleteUser();
    }

    void signIn() {
        appUserRepository.signIn();
    }

    LiveData<Boolean> getIsAppUserLoggedIn() {
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

    private void logLoginEvent() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, null);
    }
}
