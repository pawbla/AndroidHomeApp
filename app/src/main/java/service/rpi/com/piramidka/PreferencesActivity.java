package service.rpi.com.piramidka;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class PreferencesActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferencesFragment()).commit();

        //add toolbar
        Toolbar myToolbar = findViewById(R.id.preferenceToolbar);
        setSupportActionBar(myToolbar);

        //add up icon to Action Bar
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    private class PreferencesFragment extends PreferenceFragment {

        private static final String PREFERENCES_NAME = "Preferences";
        private static final String USERNAME_FIELD = "userName";
        private static final String IP_FIELD = "ipKey";
        private static final String REG_B = "reg";

        private SharedPreferences preferences;
        private EditTextPreference userName;
        private EditTextPreference ipKey;
        private Preference regB;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);

            userName = (EditTextPreference) findPreference(USERNAME_FIELD);
            ipKey = (EditTextPreference) findPreference(IP_FIELD);
            regB = findPreference(REG_B);
            initPreferences();

            regB.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.d("HomeApp","Registration button pressed." + preference.getKey());
                    return true;
                }
            });

            ipKey.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    updatePreferences(preference, newValue, ipKey);
                    return false;
                }

            });

            userName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    updatePreferences(preference, newValue, userName);
                    return true;
                }
            });
        }
        public void updatePreferences(Preference p, Object newValue, EditTextPreference t) {
            Log.d("HomeApp","Preference change save. New value: " + newValue + " key: " + p.getKey());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(p.getKey(), newValue.toString());
            editor.commit();
            t.setSummary(newValue.toString());
        }

        public void initPreferences() {
            String uName = preferences.getString(USERNAME_FIELD, "");
            String iKey = preferences.getString(IP_FIELD, "");
            userName.setText(uName);
            userName.setSummary(uName);
            ipKey.setText(iKey);
            ipKey.setSummary(iKey);
        }

    }


}
