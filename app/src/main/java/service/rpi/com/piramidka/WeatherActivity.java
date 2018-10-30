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

import service.rpi.com.piramidka.webservice.WebServiceConnector;

public class WeatherActivity extends AppCompatActivity {

    private Menu menu;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        Log.d("Apps Main", "Create options menu.");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.check_connection).setIcon(R.drawable.ic_sync);
        new WebServiceConnector(WeatherActivity.this, menu, WebServiceConnector.AUTH).execute("weatherRest");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("Apps Main", "Menu item selected: " + item.getItemId());
        switch (item.getItemId()) {
            /** Connection button on ActionBar*/
            case R.id.check_connection:
                new WebServiceConnector(WeatherActivity.this, menu, WebServiceConnector.AUTH).execute("weatherRest");
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
}
