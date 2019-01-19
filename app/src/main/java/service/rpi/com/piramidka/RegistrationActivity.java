package service.rpi.com.piramidka;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import service.rpi.com.piramidka.webservice.RegisterUser_WebServiceConnector;
import service.rpi.com.piramidka.webservice.WebServiceConnector;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        getFragmentManager().beginTransaction().replace(R.id.registrationFragmentHolder, new RegistrationActivity.RegistrationFragment()).commit();

        //add toolbar
        Toolbar myToolbar = findViewById(R.id.registrationToolbar);
        setSupportActionBar(myToolbar);

        //add up icon to Action Bar
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(R.string.preferencesTitle);
    }

    public static class RegistrationFragment extends PreferenceFragment {

        /**
         * Constants
         */
        private static final String PREFERENCES_NAME = "Preferences";
        private static final String USERNAME_FIELD = "userName";
        private static final String PASSWORD_FIELD = "userPassword";
        private static final String CONTINUE_BUTTON = "contRegistration";

        /**
         * Variable declarations
         */
        private SharedPreferences preferences;
        private EditTextPreference userName;
        private EditTextPreference userPassword;
        private Preference continueButton;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.registration);

            preferences = this.getActivity().getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);

            userName = (EditTextPreference) findPreference(USERNAME_FIELD);
            userPassword = (EditTextPreference) findPreference(PASSWORD_FIELD);
            continueButton = findPreference(CONTINUE_BUTTON);

            // Continue button
            continueButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String popupName = "Nie można przejść dalej";
                    String msgEmpty = "Użytkownika oraz hasło powinny zostać uzupełnione celem kontynuacji.";

                    Log.d("Apps","Continue button pressed." + preference.getKey());
                    //validate not empty user and password
                    if (preferences.getString(USERNAME_FIELD, "").isEmpty() || preferences.getString(PASSWORD_FIELD, "").isEmpty() ) {
                        showAlertPopup(popupName, msgEmpty);
                    } else {
                        Intent net = new Intent (getActivity(), PreferencesActivity.class);
                        startActivity(net);
                    }
                    return true;
                }
            });


            //set user name field
            userName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (validateField(newValue, getString(R.string.userSettingsTitle), 4, 40)) {
                        updatePreferences(preference, newValue, userName);
                    }
                    return true;
                }
            });

            //set password field
            userPassword.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (validateField(newValue, getString(R.string.passSettingsTitle), 8, 20)) {
                        updatePreferences(preference, newValue, userPassword);
                    }
                    return true;
                }
            });
        }

        private void updatePreferences(Preference p, Object newValue, EditTextPreference t) {
            Log.d("Apps PrefActivity","Preference change save. New value: " + newValue + " key: " + p.getKey());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(p.getKey(), newValue.toString());
            editor.commit();
            t.setSummary(newValue.toString());
        }

        private boolean validateField(Object value, String popupName, int min, int max) {
            boolean ret = true;
            final String msgEmpty = "Nie podałeś wartości.";
            final String msgIncLength = "Niepoprawna ilość znaków. \nPole powinno zawierać pomiędzy " + min + ", a " + max + " znaków.";
            final String val = (String) value;
            if (value.equals("") || value == null) {
                showAlertPopup(popupName, msgEmpty);
                ret = false;
            } else if (val.length() < min || val.length() > max) {
                showAlertPopup(popupName, msgIncLength);
                ret = false;
            }
            return ret;
        }

        private void showAlertPopup(String popupName, String msg) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(popupName);
            builder.setMessage(msg);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
        }


    }
}
