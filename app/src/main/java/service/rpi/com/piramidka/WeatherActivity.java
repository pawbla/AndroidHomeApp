package service.rpi.com.piramidka;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import service.rpi.com.piramidka.webservice.WebServiceConnector;

public class WeatherActivity extends AppCompatActivity {

    private Menu menu;
    private TextView inTemp;
    private TextView inHum;
    private TextView outTemp;
    private TextView outHum;
    private TextView pressure;
    private TextView measTime;
    private TextView measDate;
    private static boolean inErrFlag;
    private static boolean outErrFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //add toolbar
        Toolbar myToolbar = findViewById(R.id.weatherToolbar);
        setSupportActionBar(myToolbar);

        //add up icon to Action Bar
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(R.string.weatherTitle);
        actionbar.setDisplayHomeAsUpEnabled(true);

        //activity controls
        inTemp = findViewById(R.id.inTempTextView);
        inHum = findViewById(R.id.inHumTextView);
        outTemp = findViewById(R.id.outTempTextView);
        outHum = findViewById(R.id.outHumTextView);
        pressure = findViewById(R.id.pressureTextView);
        measTime = findViewById(R.id.timeValueTextView);
        measDate = findViewById(R.id.dateValueTextView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        Log.d("Apps Main", "Create options menu.");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.check_connection).setIcon(R.drawable.ic_sync);
        updateMeasuredValues();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("Apps Main", "Menu item selected: " + item.getItemId());
        switch (item.getItemId()) {
            /** Connection button on ActionBar*/
            case R.id.check_connection:
                updateMeasuredValues();
                break;
    }
        return super.onOptionsItemSelected(item);
    }

    public static class WeatherFragment extends PreferenceFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            Log.d("Apps Main", "Create FRAGMENT.");
            return inflater.inflate(R.layout.weather_fragment, container, false);
        }
    }

    private void updateMeasuredValues() {
        String datas = "";
        String errorMsg = "";
        try {
           datas = new WebServiceConnector(WeatherActivity.this, menu, WebServiceConnector.AUTH)
                    .execute("weatherRest").get().get(1);
        } catch (InterruptedException | ExecutionException e) {
            Log.w("Apps Weather", "An exception has occured during getting measurements: " + e);
            Toast.makeText(WeatherActivity.this, "Wystąpił problem podczas odczytu danych.", Toast.LENGTH_LONG).show();
        }
        Log.d("Apps Weather", "Update data measured for: " + datas);
        Log.d("Apps ERROR_FLAG" , "IN Error flag 1: " + inErrFlag);
        try {
            JSONObject json = new JSONObject(datas);
            JSONObject inSens = json.getJSONObject("inSensor");
            JSONObject outSens = json.getJSONObject("outSensor");
            if (inSens.getInt("statusCode") == 200) {
                if (inErrFlag == true) {
                    Log.d("Apps Err_flag" , "IN Error flag 2: " + inErrFlag);
                    refreshRawColour(R.id.tableRow1, R.color.tabBack);
                    refreshRawColour(R.id.tableRow2, R.color.tabBack);
                    refreshRawColour(R.id.tableRow7, R.color.tabBack);
                }
                inTemp.setText(inSens.optString("temperature"));
                inHum.setText(inSens.optString("humidity"));
                pressure.setText(inSens.optString("pressure"));
                measTime.setText(inSens.optString("date").substring(0,5));
                measDate.setText(inSens.optString("date").substring(6,11));
            } else {
                inErrFlag = true;
                Log.d("Apps ERROR_FLAG" , "IN Error flag 3 : " + inErrFlag);
                refreshRawColour(R.id.tableRow1, R.color.tabBackError);
                refreshRawColour(R.id.tableRow2, R.color.tabBackError);
                refreshRawColour(R.id.tableRow7, R.color.tabBackError);
                errorMsg = "Problem z odczytem z czujnika wewnątrz: Error Code: " + inSens.getInt("statusCode")
                + "\nAktualny odczyt: " + inSens.optString("date");
            }
            if (outSens.getInt("statusCode") == 200) {
                if (outErrFlag == true) {
                    refreshRawColour(R.id.tableRow4, R.color.tabBack);
                    refreshRawColour(R.id.tableRow6, R.color.tabBack);
                }
                outTemp.setText(outSens.optString("temperature"));
                outHum.setText(outSens.optString("humidity"));
                measTime.setText(outSens.optString("date").substring(0,5));
                measDate.setText(outSens.optString("date").substring(6,11));
            } else {
                outErrFlag = true;
                refreshRawColour(R.id.tableRow4, R.color.tabBackError);
                refreshRawColour(R.id.tableRow6, R.color.tabBackError);
                if (!errorMsg.isEmpty()) {
                    errorMsg = errorMsg + "/n";
                }
                errorMsg = errorMsg + "Problem z odczytem z czujnika na zewnątrz: Error Code: " + outSens.getInt("statusCode")
                + "\nAktualny odczyt: " + outSens.optString("date");
            }
            if (!errorMsg.isEmpty()) {
                Toast.makeText(WeatherActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Log.w("Apps Weather", "An exception has occured during JSON conversion: " + e);
            Toast.makeText(WeatherActivity.this, "Wystąpił problem podczas konwersji danych.", Toast.LENGTH_LONG).show();
        }
    }

    private void refreshRawColour(int raw, int color) {
        final TableRow tableRow = findViewById(raw);
        tableRow.setBackgroundColor(color);
    }
}
