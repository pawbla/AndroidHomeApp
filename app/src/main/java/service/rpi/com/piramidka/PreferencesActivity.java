package service.rpi.com.piramidka;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


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

    public static class PreferencesFragment extends PreferenceFragment {

        private static final String PREFERENCES_NAME = "Preferences";
        private static final String USERNAME_FIELD = "userName";
        private static final String PASSWORD_FIELD = "userPassword";
        private static final String IP_FIELD = "ipKey";
        private static final String REG_B = "reg";

        private SharedPreferences preferences;
        private EditTextPreference userName;
        private EditTextPreference userPassword;
        private EditTextPreference ipKey;
        private Preference regB;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            preferences = this.getActivity().getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);

            userName = (EditTextPreference) findPreference(USERNAME_FIELD);
            userPassword = (EditTextPreference) findPreference(PASSWORD_FIELD);
            ipKey = (EditTextPreference) findPreference(IP_FIELD);
            regB = findPreference(REG_B);

            //set preferences field during onCreate
            initPreferences();

            // Registration button
            regB.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.d("Apps","Registration button pressed." + preference.getKey());
                    registerUser();
                    return true;
                }
            });

            // IP entered field
            ipKey.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    updatePreferences(preference, newValue, ipKey);
                    return false;
                }
            });

            //set user name field
            userName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    updatePreferences(preference, newValue, userName);
                    return true;
                }
            });

            //set password field
            userPassword.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    updatePreferences(preference, newValue, userPassword);
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

        private void initPreferences() {
            //password is not set during initialization !!!!
            String uName = preferences.getString(USERNAME_FIELD, "");
            String iKey = preferences.getString(IP_FIELD, "");
            userName.setText(uName);
            userName.setSummary(uName);
            ipKey.setText(iKey);
            ipKey.setSummary(iKey);
        }

        private void registerUser() {
            List<String> response = new ArrayList<>();
            try {
                response = new WebServiceHandler(getActivity()).execute("registrationRest", "username", prepareUserName(preferences.getString(USERNAME_FIELD, "")), "password", preferences.getString(PASSWORD_FIELD, "")).get();
            } catch (Exception e) {
                Log.d("Apps PrefActivity", "An Exception has occured during user's registered " + e);
            } finally {
                prepareToastPopup(response.get(0));
            }
            Log.d("Apps PrefActivity", "Response: " + response);
        }

        //Prepare userName to be sent via WebServiceHandler
        //get device serial number -- check this method on real device !!!!!
        private static String prepareUserName (String prefName) {
            String serialNumber;
            try {
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);

                serialNumber = (String) get.invoke(c, "gsm.sn1");
                if (serialNumber.equals(""))
                    serialNumber = (String) get.invoke(c, "ril.serialnumber");
                if (serialNumber.equals(""))
                    serialNumber = (String) get.invoke(c, "ro.serialno");
                if (serialNumber.equals(""))
                    serialNumber = (String) get.invoke(c, "sys.serialnumber");
                if (serialNumber.equals(""))
                    serialNumber = Build.SERIAL;
                // If none of the methods above worked
                if (serialNumber.equals(""))
                    serialNumber = null;
            } catch (Exception e) {
                e.printStackTrace();
                serialNumber = null;
            }
            Log.d("Apps PrefActivity", "Serial number: " + serialNumber);
            //replace below number by variable 'serialNumber' for real device
            return ("123456"+ "_" +prefName);
        }

        private void prepareToastPopup(String response) {
            String msg;
            //An exception has occured durig getting data in Async Task
            if (response == null) {
                msg = "Problem z połączeniem podczas rejestracji użytkownika.";
            } else {
                switch (response) {
                    //Response code 200 - user registered successfully
                    case "200":
                        msg = "Użytkownik został zarejestrowany.";
                        break;
                    //User probably add into db
                    case "409":
                        msg = "Nie można zarejestrować użytkownika.";
                        break;
                    //No internet connection with serwice
                    case "0":
                        msg = "Nie można nawiązać połączenia z serwisem.";
                        break;
                    //Other response code
                    default:
                        msg = "Wystąpił problem z serwisem podczas rejestracji użytkownika. Response code " + response;
                }
            }
            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
        }
    }
}
