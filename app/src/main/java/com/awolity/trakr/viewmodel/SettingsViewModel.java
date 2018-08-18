package com.awolity.trakr.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.repository.SettingsRepository;

import javax.inject.Inject;

public class SettingsViewModel extends ViewModel {

    @Inject
    SettingsRepository settingsRepository;

    public SettingsViewModel() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
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
