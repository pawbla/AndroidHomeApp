package service.rpi.com.piramidka;

import android.os.Bundle;
import android.preference.PreferenceFragment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //add toolbar
        Toolbar myToolbar = findViewById(R.id.weatherToolbar);
        setSupportActionBar(myToolbar);

        //add up icon to Action Bar
        ActionBar actionbar = getSupportActionBar();
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
        try {
           datas = new WebServiceConnector(WeatherActivity.this, menu, WebServiceConnector.AUTH)
                    .execute("weatherRest").get().get(1);
        } catch (InterruptedException | ExecutionException e) {
            Log.w("Apps Weather", "An exception has occured during getting measurements: " + e);
            Toast.makeText(WeatherActivity.this, "Wystąpił problem podczas odczytu danych.", Toast.LENGTH_LONG).show();
        }
        Log.d("Apps Weather", "Update data measured for: " + datas);
        try {
            JSONObject json = new JSONObject(datas);
            JSONObject inSens = json.getJSONObject("inSensor");
            JSONObject outSens = json.getJSONObject("outSensor");
            inTemp.setText(inSens.optString("temperature"));
            inHum.setText(inSens.optString("humidity"));
            pressure.setText(inSens.optString("pressure"));
            outTemp.setText(outSens.optString("temperature"));
            outHum.setText(outSens.optString("humidity"));
            measDate.setText(inSens.optString("date").substring(5, 10));
            measTime.setText(inSens.optString("date").substring(11, 16));
            //2018-11-06 18:19:57
        } catch (JSONException e) {
            Log.w("Apps Weather", "An exception has occured during JSON conversion: " + e);
            Toast.makeText(WeatherActivity.this, "Wystąpił problem podczas konwersji danych.", Toast.LENGTH_LONG).show();
        }
    }
}
