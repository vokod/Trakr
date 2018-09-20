package com.awolity.trakr.view.settings;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.awolity.trakr.R;
import com.awolity.trakrutils.Constants;
import com.awolity.trakrutils.MyLog;
import com.awolity.trakrutils.Utility;
import com.awolity.trakrviews.ButtonSetting;
import com.awolity.trakrviews.RadiogroupSetting;
import com.awolity.trakrviews.SeekbarSetting;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.instabug.bug.BugReporting;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.util.Collections;

public class SettingsActivity extends AppCompatActivity
        implements DeleteAccountDialog.DeleteAccountDialogListener {

    private static final String TAG = "SettingsActivity";
    private static final int RC_SIGN_IN = 22;

    private ButtonSetting loginBs;
    private ButtonSetting logoutBs;
    private ButtonSetting deleteBs;
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
        settingsViewModel.getIsAppUserLoggedIn().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null) {
                    showUserLoginState(aBoolean);
                }
            }
        });
    }

    private void setupWidgets() {
        loginBs = findViewById(R.id.bs_login);
        logoutBs = findViewById(R.id.bs_logout);
        deleteBs = findViewById(R.id.bs_delete_account);
        accuracySs = findViewById(R.id.ss_accuracy);
        unitRs = findViewById(R.id.rs_unit);
        ButtonSetting termsBs = findViewById(R.id.bs_terms_of_use);
        ButtonSetting privacyBs = findViewById(R.id.bs_privacy_policy);
        ButtonSetting librariesBs = findViewById(R.id.bs_libraries);
        ButtonSetting contactBs = findViewById(R.id.bs_feedback);

        loginBs.setup(getString(R.string.setting_label_login),
                getString(R.string.settings_description_login),
                R.drawable.ic_login, R.drawable.ic_login_disabled, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(Collections.singletonList(
                                        new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()))
                                .setLogo(R.mipmap.ic_launcher)
                                .setPrivacyPolicyUrl("https://trakrapp.github.io/privacy.html")
                                .setTosUrl("https://trakrapp.github.io/terms.html")
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
                        //  showDeleteAccountAlertDialog();
                        DeleteAccountDialog dialog = new DeleteAccountDialog();
                        dialog.show(getSupportFragmentManager(), null);
                    }
                });

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
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://trakrapp.github.io/terms.html"));
                        startActivity(browserIntent);
                    }
                });
        privacyBs.setup(getString(R.string.setting_label_privacy_policy),
                null, R.drawable.ic_privacy_policy, 0, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://trakrapp.github.io/privacy.html"));
                        startActivity(browserIntent);
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

    private void showUserLoginState() {
        if (settingsViewModel.IsAppUserLoggedIn()) {
            // user is logged in
            loginBs.setEnabled(false);
            logoutBs.setEnabled(true);
            deleteBs.setEnabled(true);
            logoutBs.setDescription(getString(R.string.settings_description_logout_with_account,
                    settingsViewModel.getAppUser().getEmail()));
        } else {
            loginBs.setEnabled(true);
            logoutBs.setEnabled(false);
            deleteBs.setEnabled(false);
        }
    }

    private void showUserLoginState(boolean isAppUserLoggedIn) {

        loginBs.setEnabled(!isAppUserLoggedIn);
        logoutBs.setEnabled(isAppUserLoggedIn);
        deleteBs.setEnabled(isAppUserLoggedIn);
        if (isAppUserLoggedIn) {
            logoutBs.setDescription(getString(R.string.settings_description_logout_with_account,
                    settingsViewModel.getAppUser().getEmail()));
        } else {
            logoutBs.setDescription(getString(R.string.settings_description_logout));
        }
    }

    private void showAccuracySetting() {
        accuracySs.setSeekBarPosition(settingsViewModel.getAccuracy());
    }

    private void showUnitSetting() {
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
        showUserLoginState();
        showAccuracySetting();
        showUnitSetting();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                settingsViewModel.signIn();
                Utility.showToast(this, getString(R.string.login_successful));
                showUserLoginState();
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
                        settingsViewModel.getAppUser().getEmail()))
                .setCancelable(true)
                .setIcon(getDrawable(R.drawable.ic_warning))
                .setPositiveButton(getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                settingsViewModel.signOut();
                                Toast.makeText(SettingsActivity.this, getString(R.string.you_are_logged_out),
                                        Toast.LENGTH_LONG).show();
                                showUserLoginState();
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

    @Override
    public void onPasswordEntered(char[] password) {
        final FirebaseUser user = settingsViewModel.getAppUser();

        String passwordString = new String(password);
        AuthCredential credential = EmailAuthProvider
                .getCredential(settingsViewModel.getAppUser().getEmail(), passwordString);

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MyLog.d(TAG, "onSuccess");
                        settingsViewModel.deleteAccount();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        MyLog.d(TAG, "onFailure");
                        Toast.makeText(SettingsActivity.this,
                                getString(R.string.delete_account_toast_authentication_error),
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        MyLog.d(TAG, "onCanceled");
                        Toast.makeText(SettingsActivity.this,
                                getString(R.string.delete_account_toast_authentication_error),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}