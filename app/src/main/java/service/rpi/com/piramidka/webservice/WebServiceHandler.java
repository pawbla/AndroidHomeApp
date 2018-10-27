package service.rpi.com.piramidka.webservice;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/*
fetched string args: fileLocation, pair of parameters to be sent as list e.g. "key1", "val1", ...
returned list: <return code>, <message>
 */
public class WebServiceHandler extends AsyncTask<String, Void, List<String>> {

    private Context context;
    //private ProgressDialog progressDialog;
    private HttpURLConnection connection;
    List<String> response =  new ArrayList<>();

    public WebServiceHandler(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {
        Log.d("Apps WebServiceHandler","onPreExecute");
       /* progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(STYLE_SPINNER);
        progressDialog.setMessage("Łączenie z serwisem...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);*/
       Log.d("Apps WebServiceHandler","onPreExecute 2");
    }

    protected List<String> doInBackground(String... params) {
        Log.d("Apps WebServiceHandler","doInBackground. Params length: " + params.length);
        response.add(0, "");
        response.add(1, "");
        //192.168.1.60
        try {
            URL url = new URL("http://192.168.1.60:8080/" + params[0]);
            Log.d("Apps WebServiceHandler", "doInBackground - 1");
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);

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
       //progressDialog.dismiss();
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
            Log.w("HomeApp", "Convert STREAM TO STRING error has occurred: " + e);;
        }
        return stringBuilder.toString();
    }
}
