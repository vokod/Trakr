package com.awolity.trakr.view;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.awolity.trakr.R;

public class SettingsActivity extends AppCompatPreferenceActivity implements Preference.OnPreferenceChangeListener {


    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        if (preference instanceof SwitchPreference) {// Trigger the listener immediately with the preference's
            // current value.
            onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), true));
        }
        if (preference instanceof ListPreference) {// Trigger the listener immediately with the preference's
            // current value.
            onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        // Add 'general' preferences, defined in the XML file
        // addPreferencesFromResource(R.xml.pref_general);
        addPreferencesFromResource(R.xml.pref_general);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_tracking_interval)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_tracking_distance)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_accuracy)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_accuracy_filter)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_altitude_filter_param)));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
}
