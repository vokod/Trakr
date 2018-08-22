package com.awolity.trakr.view.settings;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;

import com.awolity.trakr.R;
import com.awolity.trakr.viewmodel.SettingsViewModel;
import com.awolity.trakrutils.Constants;
import com.awolity.trakrviews.ButtonSetting;
import com.awolity.trakrviews.RadiogroupSetting;
import com.awolity.trakrviews.SeekbarSetting;
import com.instabug.bug.BugReporting;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private static final int RC_SIGN_IN = 22;

    private ButtonSetting termsBs, privacyBs, librariesBs, contactBs;
    private SeekbarSetting accuracySs;
    private RadiogroupSetting unitRs;
    private SettingsViewModel settingsViewModel;

    public static Intent getStarterIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupWidgets();
        settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
    }

    private void setupWidgets() {
        accuracySs = findViewById(R.id.ss_accuracy);
        unitRs = findViewById(R.id.rs_unit);
        termsBs = findViewById(R.id.bs_terms_of_use);
        privacyBs = findViewById(R.id.bs_privacy_policy);
        librariesBs = findViewById(R.id.bs_libraries);
        contactBs = findViewById(R.id.bs_feedback);

        accuracySs.setup(getString(R.string.setting_label_accuracy),
                getString(R.string.settings_description_accuracy),
                R.drawable.ic_accuracy,
                2, 0, new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            settingsViewModel.setAccuracy(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

        unitRs.setup(getString(R.string.setting_label_units),
                getString(R.string.setting_description_units),
                R.drawable.ic_unit,
                getString(R.string.radiobutton_label_metric),
                getString(R.string.radiobutton_label_implerial),
                0, new RadiogroupSetting.RadiogroupSettingListener() {
                    @Override
                    public void OnRadioButtonClicked(int no) {
                        if (no == 0) {
                            settingsViewModel.setUnit(Constants.UNIT_METRIC);
                        } else {
                            settingsViewModel.setUnit(Constants.UNIT_IMPERIAL);
                        }
                    }
                });

        termsBs.setup(getString(R.string.setting_label_terms_of_use), null,
                R.drawable.ic_terms_of_use, 0, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
        privacyBs.setup(getString(R.string.setting_label_privacy_policy),
                null, R.drawable.ic_privacy_policy, 0, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
        librariesBs.setup(getString(R.string.setting_label_libraries),
                getString(R.string.setting_description_libraries),
                R.drawable.ic_libraries, 0, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new LibsBuilder()
                                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                .withAboutIconShown(true)
                                .withAboutVersionShown(true)
                                .withAboutDescription(getString(R.string.setting_description_libraries))
                                .withActivityTitle(getString(R.string.setting_label_libraries))
                                .start(SettingsActivity.this);
                    }
                });
        contactBs.setup(getString(R.string.setting_label_feedback),
                getString(R.string.setting_description_feedback),
                R.drawable.ic_feedback, 0, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BugReporting.invoke();
                    }
                });
    }

    private void showAccuracySetting() {
        accuracySs.setSeekBarPosition(settingsViewModel.getAccuracy());
    }

    private void showUnitSetting(){
        int unit = settingsViewModel.getUnit();
        if (unit == Constants.UNIT_METRIC) {
            unitRs.setSelected(0);
        } else if (unit == Constants.UNIT_IMPERIAL) {
            unitRs.setSelected(1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        showAccuracySetting();
        showUnitSetting();
    }
}