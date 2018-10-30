package service.rpi.com.piramidka.webservice;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

class AuthenticationService {

    private static final String PREFERENCES_NAME = "Preferences";
    private static final String PASSWORD_FIELD = "userPassword";

    static String getHeader(Context context, String userName) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        return "Basic " + new String(android.util.Base64.encode((userName +":"+ preferences.getString(PASSWORD_FIELD, null)).getBytes(), android.util.Base64.NO_WRAP));
    }
}
