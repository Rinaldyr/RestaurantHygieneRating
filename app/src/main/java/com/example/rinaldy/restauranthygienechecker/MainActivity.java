package com.example.rinaldy.restauranthygienechecker;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIMEOUT = 500;
    private final int FINE_LOCATION_PERMISSION = 1;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("pageNumber", 1).apply();

        if (!sharedPreferences.contains("locationPermission")) { // First Time opening app?
            askForPermission();
        } else {
            if (sharedPreferences.getBoolean("locationPermission", false)) { // Granted
                locationGrantedActivity();
            } else {
                locationDeniedActivity();
            }
        }
    }

    public void askForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationGrantedActivity();
        } else {
            requestLocationPermissions();
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

    public void locationGrantedActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (!isLocationServiceEnabled()) {
                    intent = new Intent(MainActivity.this, SearchActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, ViewActivity.class);
                    intent.putExtra("firstOpen", true);
                }
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }, SPLASH_TIMEOUT);
    }

    public void locationDeniedActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(homeIntent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }, SPLASH_TIMEOUT);
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
                        editor = sharedPreferences.edit();
                        editor.putBoolean("locationPermission", true);
                        editor.apply();
                        locationGrantedActivity();
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
                                            editor = sharedPreferences.edit();
                                            editor.putBoolean("locationPermission", false);
                                            editor.apply();
                                            locationDeniedActivity();
                                        }
                                    })
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

}
