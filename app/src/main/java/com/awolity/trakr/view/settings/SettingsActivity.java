package com.awolity.trakr.view.settings;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.awolity.trakr.R;
import com.awolity.trakr.customviews.ButtonSetting;
import com.awolity.trakr.customviews.RadiogroupSetting;
import com.awolity.trakr.customviews.SeekbarSetting;
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.utils.Utility;
import com.awolity.trakr.viewmodel.AppUserViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private static final int RC_SIGN_IN = 22;

    private ButtonSetting loginBs, logoutBs, deleteBs, termsBs, privacyBs, librariesBs, contactBs;
    private SeekbarSetting accuracySs;
    private RadiogroupSetting unitRs;
    private AppUserViewModel appUserViewModel;

    public static Intent getStarterIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupWidgets();
        appUserViewModel = ViewModelProviders.of(this).get(AppUserViewModel.class);
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
                R.drawable.ic_login, R.drawable.ic_login_disabled, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()))
                                .build(), RC_SIGN_IN);
                    }
                });

        logoutBs.setup(getString(R.string.setting_label_logout),
                getString(R.string.settings_description_logout),
                R.drawable.ic_logout, R.drawable.ic_logout_disabled, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLogoutAlertDialog();
                    }
                });

        deleteBs.setup(getString(R.string.setting_label_delete_account),
                getString(R.string.settings_description_delete_account),
                R.drawable.ic_delete_account, R.drawable.ic_delete_account_disabled,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

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

        unitRs.setup(getString(R.string.setting_label_units),
                getString(R.string.setting_description_units),
                R.drawable.ic_unit,
                getString(R.string.radiobutton_label_metric),
                getString(R.string.radiobutton_label_implerial),
                0, new RadiogroupSetting.RadiogroupSettingListener() {
                    @Override
                    public void OnRadioButtonClicked(int no) {
                        MyLog.d(TAG, "OnRadioButtonClicked: " + no);
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

                    }
                });
        contactBs.setup(getString(R.string.setting_label_feedback),
                getString(R.string.setting_description_feedback),
                R.drawable.ic_feedback, 0, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
    }

    private void checkUserState() {
        if (appUserViewModel.IsAppUserLoggedIn()) {
            // user is logged in
            loginBs.setEnabled(false);
            logoutBs.setEnabled(true);
            deleteBs.setEnabled(true);
            logoutBs.setDescription(getString(R.string.settings_description_logout_with_account,
                    appUserViewModel.getAppUser().getEmail()));
        } else {
            loginBs.setEnabled(true);
            logoutBs.setEnabled(false);
            deleteBs.setEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                appUserViewModel.signIn();
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // UserEntity pressed back button
                    Utility.showToast(this, getString(R.string.login_error_cancel));
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Utility.showToast(this, getString(R.string.login_error_no_internet));
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Utility.showToast(this, getString(R.string.login_error_unknown_error));
                    return;
                }
            }
            Utility.showToast(this, getString(R.string.login_error_unknown_response));
        }
    }

    private void showLogoutAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle(getString(R.string.logout_dialog_title));
        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.logout_dialog_message,
                        appUserViewModel.getAppUser().getEmail()))
                .setCancelable(true)
                .setIcon(getDrawable(R.drawable.ic_warning))
                .setPositiveButton(getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        appUserViewModel.signOut();
                        Toast.makeText(SettingsActivity.this, getString(R.string.you_are_logged_out),
                                Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showDeleteAccountAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle(getString(R.string.delete_account_dialog_title));
        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.delete_account_dialog_message))
                .setCancelable(true)
                .setIcon(getDrawable(R.drawable.ic_warning))
                .setPositiveButton(getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                appUserViewModel.deleteAccount();
                                Toast.makeText(SettingsActivity.this,
                                        getString(R.string.you_are_logged_out),
                                        Toast.LENGTH_LONG).show();
                            }
                        })
                .setNegativeButton(getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
