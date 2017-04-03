package app.moysof.mydrivingblows;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
        toolbar.setContentInsetsAbsolute(
                CommonUtilities.convertDpToPixel(72, this), 0);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(8f);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.preferencesLayout, new SettingsFragment())
                .commit();

    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle paramBundle) {
            super.onCreate(paramBundle);
            addPreferencesFromResource(R.xml.pref_general);

            if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getString
                    ("username", "").contains(" ")) {
                findPreference("change_username").setEnabled(true);
            } else {
                findPreference("change_username").setEnabled(false);
            }

            findPreference("change_username").setOnPreferenceClickListener(new Preference
                    .OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity(), UsernameActivity.class));
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return false;
    }

}