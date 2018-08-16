package com.awolity.trakr.view.settings;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;

import com.awolity.trakr.R;
import com.awolity.trakr.customviews.ButtonSetting;
import com.awolity.trakr.customviews.RadiogroupSetting;
import com.awolity.trakr.customviews.SeekbarSetting;
import com.awolity.trakr.utils.MyLog;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private ButtonSetting loginBs, logoutBs, deleteBs, termsBs, privacyBs, librariesBs, contactBs;
    private SeekbarSetting accuracySs;
    private RadiogroupSetting unitRs;

    public static Intent getStarterIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupWidgets();
    }

    private void setupWidgets() {
        loginBs = findViewById(R.id.bs_login);
        logoutBs = findViewById(R.id.bs_logout);
        deleteBs = findViewById(R.id.bs_delete_account);
        accuracySs = findViewById(R.id.ss_accuracy);
        unitRs = findViewById(R.id.rs_unit);
        termsBs = findViewById(R.id.bs_terms_of_use);
        privacyBs = findViewById(R.id.bs_privacy_policy);
        librariesBs = findViewById(R.id.bs_libraries);
        contactBs = findViewById(R.id.bs_feedback);

        loginBs.setup(getString(R.string.setting_label_login),
                getString(R.string.settings_description_login),
                R.drawable.ic_login);

        logoutBs.setup(getString(R.string.setting_label_logout),
                getString(R.string.settings_description_logout),
                R.drawable.ic_logout);

        deleteBs.setup(getString(R.string.setting_label_delete_account),
                getString(R.string.settings_description_delete_account),
                R.drawable.ic_delete_account);

        accuracySs.setup(getString(R.string.setting_label_accuracy),
                getString(R.string.settings_description_accuracy),
                R.drawable.ic_accuracy,
                2, 0, new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

        unitRs.setup("Units", "Do you prefer km of miles?", R.drawable.ic_unit, "Metric", "Imperial",
                0, new RadiogroupSetting.RadiogroupSettingListener() {
                    @Override
                    public void OnRadioButtonClicked(int no) {
                        MyLog.d(TAG, "OnRadioButtonClicked: " + no);
                    }
                });

        termsBs.setup("Terms of use", null, R.drawable.ic_terms_of_use);
        privacyBs.setup("Privacy policy", null, R.drawable.ic_privacy_policy);
        librariesBs.setup("Libraries", "The open source libraries that made Trakr possible",
                R.drawable.ic_libraries);
        contactBs.setup("Feedback and report", "Have a problem? Want to suggest an improvement? Tap here!",
                R.drawable.ic_feedback);
    }
}
