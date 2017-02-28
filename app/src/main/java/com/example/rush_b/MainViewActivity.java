package com.example.rush_b;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.os.ResultReceiver;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.rush_b.adapter.MainViewPagerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainViewActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            LOCATION = resultData.getString(Constants.RESULT_DATA_KEY);
        }
    }

    public class FetchWeatherData extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonString;
            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/city?id=1650357&appid=e472f1b91e665b8c44ab2cd83d2fa03d");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonString = buffer.toString();
                return jsonString;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (ProtocolException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            WEATHER = s;
        }
    }
    public static final int TAB_HOME = 0;
    public static final int TAB_COMMUNICATION = 1;
    public static final int TAB_STATISTIC = 2;

    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_BOMB_DEFUSED = "Bomb Defused";
    public static final String EXTRA_TIME_SPENT = "Time Spent";
    public static final String EXTRA_NAME = "name";
    public static String LOCATION;
    public static String WEATHER;

    private GoogleApiClient mGoogleApiClient;
    private AddressResultReceiver mResultReceiver;
    private Location mCurrentLocation;

    private Toolbar toolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        MainViewPagerAdapter adapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        new FetchWeatherData().execute();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addOnConnectionFailedListener(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.barcode_scan_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.barcode_scanner:
                Intent intent = new Intent(getApplicationContext(), BarcodeScanner.class);
                startActivity(intent);
                return true;
            default:
                return true;
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Permission Denied",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mCurrentLocation != null) {
            mResultReceiver = new AddressResultReceiver(new Handler());
            Intent intent = new Intent(this, FetchAddressIntentService.class);
            intent.putExtra(Constants.RECEIVER, mResultReceiver);
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, mCurrentLocation);
            startService(intent);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }
}
