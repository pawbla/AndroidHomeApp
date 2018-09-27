package service.rpi.com.piramidka;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

public class WebServiceHandler extends AsyncTask<String, Void, String> {

    //private ProgressBar progressBar;
    private Context context;
    //private Dialog dialog;
    private ProgressDialog progressDialog;

    public WebServiceHandler(Context context) {
        this.context = context;

    }

    protected void onPreExecute() {
        Log.d("Apps WebServiceHandler","onPreExecute");
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
    }

    protected String doInBackground(String... params) {
        Log.d("Apps WebServiceHandler","doInBackground");
        String response = "";
        //establish connection "http://jsonplaceholder.typicode.com/posts/1"
        try {
            URL url = new URL("http://192.168.1.135:8080/registrationRest");
            Log.d("Apps WebServiceHandler","doInBackground - 1");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            Log.d("Apps WebServiceHandler","doInBackground - 2");
            //connection.setRequestProperty("Authorization", header);
            connection.setConnectTimeout(10000);
            //allow to send datas
                //connection.setDoOutput(true);
            //set request property type
            Log.d("Apps WebServiceHandler","doInBackground - 3");
            //set request method
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            //create objects to sending datas
            JSONObject data = new JSONObject();
            data.put("username", "jakisUser");
            data.put("password", "jakisPassword");
            //send object
            Log.d("Apps WebServiceHandler","doInBackground - 4");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            writer.write(data.toString());
            writer.close();
            connection.connect();
            Log.d("Apps WebServiceHandler","doInBackground - 5: " + connection.getResponseCode());
            if (connection.getResponseCode() != 200) {
                return null;
            }
            InputStream in = new BufferedInputStream(connection.getInputStream());
            response = streamToString(in);
            Log.d("Apps WebServiceHandler","Response code: " + connection.getResponseCode() + " resp:" + response);
            connection.disconnect();
        } catch (Exception e) {
            Log.d("Apps WebServiceHandler","Exception has appeared: " + e);
        }
        return response;
    }

    protected void onPostExecute(String p) {
        Log.d("Apps WebServiceHandler","OnPostExecute");
        //progressBar.setVisibility(ProgressBar.INVISIBLE);
        //dialog.dismiss();
        progressDialog.dismiss();
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
            Log.d("HomeApp", "Convert STREAM TO STRING error has occured: " + e);;
        }
        return stringBuilder.toString();
    }
}
