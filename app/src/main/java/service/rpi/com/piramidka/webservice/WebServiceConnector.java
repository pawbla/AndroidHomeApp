package service.rpi.com.piramidka.webservice;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 *  Class provides method to connect with webService
 */
public class WebServiceConnector implements WebServiceConnectorInterface {

    private SharedPreferences preferences;
    private Context context;

    private static final String PREFERENCES_NAME = "Preferences";
    private static final String USERNAME_FIELD = "userName";

    protected List<String> response;

    public WebServiceConnector(Context context) {
        response = new ArrayList<>();
        this.context = context;
    }

    public String getStatusCode () {
        return response.get(0);
    }

    public String getReceivedMessage () {
            return response.get(1);
    }

    /**
     * Prepare username as a string based on device serial ID and entered userName
     * @return username
     */
    public String prepareUserName () {
        //get preferences
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
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
        return ("123456"+ "_" +preferences.getString(USERNAME_FIELD, null));
    }

    public void connect (String... data) {
        try {
            response = new WebServiceHandler(context).execute(data).get();
        } catch (CancellationException | ExecutionException | InterruptedException e) {
            response.add(0, "10");
            response.add(1, e.toString());
            Log.d("Apps PrefActivity", "An Exception has occured during user's registered " + e);
        }
        Log.d("Apps AbsWebServConn", "Response:" + response);
    }

    public void showToastPopup () {
        String msg;
        //An exception has occured durig getting data in Async Task
        if (response.get(0) == null) {
            msg = "Problem z połączeniem z serwisem.";
        } else {
            switch (response.get(0)) {
                //ok
                case "200":
                    msg = null;
                    break;
                //User has not add
                case "401":
                    msg = "Brak dostępu, użytkownik niezarejestrowany.";
                    break;
                //User add but disabled
                case "403":
                    msg = "Użytkownik nieaktywny - brak dostępu.";
                    break;
                //No internet connection with serwice
                case "0":
                    msg = "Nie można nawiązać połączenia z serwisem.";
                    break;
                //Other response code
                default:
                    msg = "Wystąpił problem z serwisem. Response code " + response;
            }
        }
        if (msg != null) {
            Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }
    }
}
