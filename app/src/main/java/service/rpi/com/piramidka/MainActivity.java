package service.rpi.com.piramidka;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import service.rpi.com.piramidka.webservice.WebServiceConnector;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private TextView tv;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Apps Main", "Create onCreate.");
        setContentView(R.layout.activity_login);

        //add toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //add action bar
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionbar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        tv = findViewById(R.id.titleTextView);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        switch (menuItem.getItemId()) {
                            case R.id.weather:
                                Intent net = new Intent (MainActivity.this, WeatherActivity.class);
                                startActivity(net);
                                break;
                            case R.id.settings:
                                Intent conf = new Intent(MainActivity.this, PreferencesActivity.class);
                                startActivity(conf);
                                break;
                            default:
                                break;
                        }
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                        Log.d("Apps Main", "Open drawer ");
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                        invalidateOptionsMenu();
                        Log.d("Apps Main", "Close drawer ");
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );
        addMainFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        Log.d("Apps Main", "Create options menu.");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.check_connection).setIcon(R.drawable.ic_sync);
        new WebServiceConnector(MainActivity.this, menu).execute("registrationCheck/" + WebServiceConnector.prepareUserName(MainActivity.this));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("Apps Main", "Menu item selected: " + item.getItemId());
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.check_connection:
                new WebServiceConnector(MainActivity.this, menu).execute("registrationCheck/" + WebServiceConnector.prepareUserName(MainActivity.this));
        }
        return super.onOptionsItemSelected(item);
    }

    private void addMainFragment() {
        Fragment mainFragment = new MainFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragmentHolder, mainFragment, "visible_fragment");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
}