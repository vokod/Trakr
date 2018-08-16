package com.awolity.trakr.view.settings;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;

import com.awolity.trakr.R;
import com.awolity.trakr.customviews.ButtonSetting;
import com.awolity.trakr.customviews.SeekbarSetting;

public class SettingsActivity extends AppCompatActivity {

    private ButtonSetting loginBs, logoutBs, deleteBs;
    private SeekbarSetting accuracySs;

    public static Intent getStarterIntent(Context context){
        Intent intent = new Intent(context, SettingsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupWidgets();
    }

    private void setupWidgets(){
        loginBs = findViewById(R.id.bs_login);
        logoutBs = findViewById(R.id.bs_logout);
        deleteBs = findViewById(R.id.bs_delete_account);
        accuracySs = findViewById(R.id.bs_accuracy);

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
                3, 0, new SeekBar.OnSeekBarChangeListener() {
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
    }
}
