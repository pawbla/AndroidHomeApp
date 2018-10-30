package service.rpi.com.piramidka.webservice;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import service.rpi.com.piramidka.R;

/**
 *  Class provides method to connect with webService
 */
public class WebServiceConnector extends AsyncTask<String, Void, List<String>> {

    private Context context;
    private MenuItem menuItem;
    private HttpURLConnection connection;
    private String iP;
    private boolean auth;

    private static final String PREFERENCES_NAME = "Preferences";
    private static final String USERNAME_FIELD = "userName";
    private static final String IP_FIELD = "ipKey";

    public static final boolean AUTH = true;
    public static final boolean NO_AUTH = false;

    protected List<String> response;

    public WebServiceConnector(Context context, Menu menu, boolean auth) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        response = new ArrayList<>();
        this.context = context;
        this.auth = auth;
        this.menuItem =  menu.findItem(R.id.check_connection);
        iP = preferences.getString(IP_FIELD, null);
    }

    /**
     * Prepare username as a string based on device serial ID and entered userName
     * @return username
     */
    public static String prepareUserName (Context context) {
        //get preferences
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
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

    private void updateConnectionIcon () {
        if ("200".equals(response.get(0))) {
            menuItem.setIcon(R.drawable.ic_sync);
        } else {
            menuItem.setIcon(R.drawable.ic_sync_problem);
        }
    }

    protected void showToastPopup () {
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

    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("Apps WebServiceHandler","onPreExecute");
        menuItem.setActionView(R.layout.progress_bar);
        menuItem.expandActionView();
        Log.d("Apps WebServiceHandler","onPreExecute 2");
    }

    protected List<String> doInBackground(String... params) {
        Log.d("Apps WebServiceHandler","doInBackground. Params length: " + params.length);
        response.add(0, "");
        response.add(1, "");
        //192.168.1.60
        try {
            //192.168.1.60
            URL url = new URL("http://" + iP + ":8080/" + params[0]);
            Log.d("Apps WebServiceHandler", "doInBackground - 1");
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            //set authorization header
            if (auth) {
                connection.setRequestProperty("Authorization", AuthenticationService.getHeader(context, prepareUserName(context)));
            }
            //set request property type
            Log.d("Apps WebServiceHandler", "doInBackground - 2");
            //set params as POST data in other case only receive datas via GET request
            if (params.length > 1) {
                Log.d("Apps WebServiceHandler", "doInBackground - 3 - POST");

                //set request method
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //create objects to sending datas
                JSONObject data = new JSONObject();
                //prepare data to be sent via POST - assign pair of datas
                for (int i = 1; i < params.length; i = i + 2) {
                    Log.d("Apps WebServiceHandler", "doInBackground - 4 - put data " + i);
                    data.put(params[i], params[i+1]);
                }

                //send object
                Log.d("Apps WebServiceHandler", "doInBackground - 5");
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                writer.write(data.toString());
                writer.close();
            }
            connection.connect();
            Log.d("Apps WebServiceHandler", "doInBackground - 6: " + connection.getResponseCode());
            response.add(0, Integer.toString(connection.getResponseCode()));
            if (connection.getResponseCode() == 200) {
                //fetch data
                InputStream in = new BufferedInputStream(connection.getInputStream());
                response.add(1, streamToString(in));
                //close connection
                Log.d("Apps WebServiceHandler","Response code: " + connection.getResponseCode() + " resp:" + response);
                connection.disconnect();
            }
        } catch (Exception e) {
            response.add(0, "0");
            Log.w("Apps WebServiceHandler","Exception has appeared: " + e);
        }
        Log.w("Apps WebServiceHandler","Response generated: response code " + response.get(0) + " message: " + response.get(1));
        return response;
    }

    protected void onPostExecute( List<String> p) {
        Log.d("Apps WebServiceHandler","OnPostExecute");
        menuItem.collapseActionView();
        menuItem.setActionView(null);
        showToastPopup ();
        updateConnectionIcon();
    }

    private String streamToString (InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        Log.d("HomeApp", "Stream to string");
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            reader.close();
        } catch (IOException e) {
            Log.w("HomeApp", "Convert STREAM TO STRING error has occurred: " + e);
        }
        return stringBuilder.toString();
    }
}
