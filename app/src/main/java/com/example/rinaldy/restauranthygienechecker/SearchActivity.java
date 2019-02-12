package com.example.rinaldy.restauranthygienechecker;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

public class SearchActivity extends AppCompatActivity {
    private final int FINE_LOCATION_PERMISSION = 1;

    LocationManager locationManager;
    LocationListener locationListener;
    SearchView searchName;
    SearchView searchPlace;

    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        toolbar.setTitle("Search");
        setSupportActionBar(toolbar);

        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Search");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Button nearMeBtn = (Button) findViewById(R.id.near_me_btn);
        nearMeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    requestLocationPermissions();
                } else {
                    actionOnLocationEnabled();
                }
            }
        });

        Button advSearchBtn = (Button) findViewById(R.id.home_advance_search_btn);
        advSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, AdvancedSearchActivity.class);
                startActivity(intent);
            }
        });

        searchName = (SearchView) findViewById(R.id.searchName);
        searchName.setQueryHint("Enter a business name");
        searchName.setFocusable(true);
        searchName.setIconified(false);
        searchName.requestFocusFromTouch();
        searchName.setOnQueryTextListener(searchOnEnterListener);

        searchPlace = (SearchView) findViewById(R.id.searchPlace);
        searchPlace.setQueryHint("And/Or an address");
        searchPlace.setFocusable(true);
        searchPlace.setIconified(false);
        searchPlace.setOnQueryTextListener(searchOnEnterListener);

    }

    public void actionOnLocationEnabled() {
        if (isLocationServiceEnabled()) {
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }
                Intent intent = new Intent(SearchActivity.this, ViewActivity.class);
                getPreferences(Context.MODE_PRIVATE).edit().putInt("pageNumber", 1).apply();
                intent.putExtra("title", "Nearby");
                intent.putExtra("search", EndPoint.URLEstablishmentsByLocation(longitude, latitude));
                intent.putExtra("firstOpen", true);
                startActivity(intent);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Location is disabled")
                    .setMessage("You need to turn on location to use this feature.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .setCancelable(false)
                    .create()
                    .show();
        }
    }

    public boolean isLocationServiceEnabled() {
        LocationManager locationManager = null;
        boolean gps_enabled = false, network_enabled = false;

        if (locationManager == null)
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return gps_enabled || network_enabled;

    }

    public void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                        editor.putBoolean("locationPermission", true);
                        editor.apply();
                        actionOnLocationEnabled();
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            new AlertDialog.Builder(this)
                                    .setTitle("Location Permission Denied")
                                    .setMessage("This permission is used to detect your current location so that the app can show you the hygiene ratings of establishments around you.\nAre you sure you want to deny this permission?")
                                    .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            requestLocationPermissions();
                                        }
                                    })
                                    .setNegativeButton("I'm sure", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .setCancelable(false)
                                    .create()
                                    .show();
                        } else {
                            requestLocationPermissions();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent intent = new Intent(SearchActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    final SearchView.OnQueryTextListener searchOnEnterListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            Intent intent = new Intent(SearchActivity.this, ViewActivity.class);
            intent.putExtra("title", "Search");
            getPreferences(Context.MODE_PRIVATE).edit().putInt("pageNumber", 1).apply();
            String name = searchName.getQuery().toString();
            String place = searchPlace.getQuery().toString();
            String URL = EndPoint.URLEstablishmentByName(name, place);
            Log.i("DEBUG", URL);
            intent.putExtra("search", URL);
            intent.putExtra("firstOpen", true);
            startActivity(intent);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };
}
