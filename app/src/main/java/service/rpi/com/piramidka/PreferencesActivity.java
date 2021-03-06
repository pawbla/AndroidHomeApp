package service.rpi.com.piramidka;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import service.rpi.com.piramidka.webservice.RegisterUser_WebServiceConnector;
import service.rpi.com.piramidka.webservice.WebServiceConnector;


public class PreferencesActivity extends AppCompatActivity {

    private static Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        getFragmentManager().beginTransaction().replace(R.id.preferencesFragmentHolder, new PreferencesFragment()).commit();

        //add toolbar
        Toolbar myToolbar = findViewById(R.id.preferenceToolbar);
        setSupportActionBar(myToolbar);

        //add up icon to Action Bar
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(R.string.confTitle);
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        Log.d("Apps Main", "Create options menu.");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.check_connection).setVisible(false);
        return true;
    }

    public static class PreferencesFragment extends PreferenceFragment {

        private static final String PREFERENCES_NAME = "Preferences";
        private static final String USERNAME_FIELD = "userName";
        private static final String PASSWORD_FIELD = "userPassword";
        private static final String IP_FIELD = "ipKey";
        private static final String REG_B = "reg";
        private static final String RESET_BUTTON = "reset";
        private static final String LOGGED_USER = "logUser";
        private static final String REGISTRATION_EMAIL = "android@android.device";

        private SharedPreferences preferences;
        private EditTextPreference ipKey;
        private Preference regB;
        private Preference resetButton;
        private EditTextPreference logUser;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            preferences = this.getActivity().getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);

            ipKey = (EditTextPreference) findPreference(IP_FIELD);
            regB = findPreference(REG_B);
            resetButton = findPreference(RESET_BUTTON);
            logUser = (EditTextPreference)findPreference(LOGGED_USER);

            //set preferences field during onCreate
            initPreferences();

            // Registration button
            regB.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.d("Apps","Registration button pressed." + preference.getKey());
                    menu.findItem(R.id.check_connection).setIcon(R.drawable.ic_sync).setVisible(true);
                    new RegisterUser_WebServiceConnector(getActivity(), menu).execute("registrationRest",
                            "username",WebServiceConnector.prepareUserName(getActivity()), "password", preferences.getString(PASSWORD_FIELD, ""), "email", REGISTRATION_EMAIL);
                    return true;
                }
            });

            // Reset button button
            resetButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.d("Apps","Reset button pressed." + preference.getKey());
                    yesNoAlertPopup().show();
                    return true;
                }
            });

            // IP entered field
            ipKey.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (validateField(newValue, getString(R.string.ipSettingsTitle), 6, 100)) {
                        updatePreferences(preference, newValue, ipKey);
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

        private void initPreferences() {
            //password is not set during initialization !!!!
            String iKey = preferences.getString(IP_FIELD, "");
            logUser.setTitle("Użytkownik: " + preferences.getString(USERNAME_FIELD, ""));
            ipKey.setText(iKey);
            ipKey.setSummary(iKey);
        }

        private AlertDialog yesNoAlertPopup() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.yesNoMsg)
                    .setTitle(R.string.yesNoTitle);
            builder.setPositiveButton(R.string.yesNoYes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked YES button
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(USERNAME_FIELD, "");
                    editor.putString(PASSWORD_FIELD, "");
                    editor.apply();
                }
            });
            builder.setNegativeButton(R.string.yesNoNo, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked NO button
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            return dialog;
        }
    }
}
