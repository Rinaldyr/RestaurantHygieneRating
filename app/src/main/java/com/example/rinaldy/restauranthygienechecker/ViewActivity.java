package com.example.rinaldy.restauranthygienechecker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewActivity extends AppCompatActivity {
    private final int FINE_LOCATION_PERMISSION = 1;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    SharedPreferences sharedPreferences;

    private String title;
    private String search;
    private int pageNumber;

    private ArrayList<Establishment> mEstablishments;
    private OnePage.Meta mMeta;

    LocationManager locationManager;
    LocationListener locationListener;
    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        if (getIntent().getBooleanExtra("firstOpen", false)) {
            pageNumber = 1;
        } else {
            pageNumber = sharedPreferences.getInt("pageNumber", 1);
        }
        Log.i("VIEW", "pagenum: " + pageNumber);

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.title = getIntent().getStringExtra("title");
        if (title == null) {
            title = "Nearby";
        }
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryLight));
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        this.search = getIntent().getStringExtra("search");
        if (search != null) {
            new MyTask(this, search).execute();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                requestLocationPermissions();
            } else {
                actionOnLocationEnabled();
            }
        }

        this.pageNumber = getIntent().getIntExtra("pageNumber", 1);
    }

    public void initialiseApplication(String URL) {
        new MyTask(this, URL).execute();
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
                String URL = EndPoint.URLEstablishmentsByLocation(longitude, latitude);
                Log.i("DEBUG", URL);
                initialiseApplication(URL);
            } catch (SecurityException e) {
                Log.e("PERMS", e.getMessage());
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
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(ViewActivity.this, SearchActivity.class);
                            startActivity(intent);
                        }
                    })
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
    public void onBackPressed() {
        Log.i("VIEW", "back pressed, pagenum: " + pageNumber);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about: {
                Intent intent = new Intent(ViewActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.search: {
                Intent intent = new Intent(ViewActivity.this, SearchActivity.class);
                intent.putExtra("search_title", title);
                startActivity(intent);
                return true;
            }
            case android.R.id.home: {
                onBackPressed();
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    ListViewFragment lvf = new ListViewFragment();
                    lvf.initialiseList(mEstablishments, mMeta);
                    return lvf;
                case 1:
                    MapViewFragment mvf = new MapViewFragment();
                    mvf.initialiseList(mEstablishments);
                    return mvf;
                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    static class MyTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;
        WeakReference<ViewActivity> activity;
        String URL;
        boolean failed = false;
        int pageNumber;

        public MyTask(ViewActivity activity, String URL) {
            this.activity = new WeakReference<>(activity);
            dialog = new ProgressDialog(activity);
            dialog.setCancelable(false);
            Log.i("VIEW", "pageNumber: " + pageNumber + " ActPageNum: " + activity.pageNumber);
            pageNumber = activity.pageNumber;
            activity.sharedPreferences.edit().putString("search", URL).apply();
            this.URL = URL + EndPoint.URLPaging(pageNumber);
            Log.i("VIEW", this.URL);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Fetching data...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            StringRequest getRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            OnePage page = GsonUtils.getInstance().getGson().fromJson(response, OnePage.class);
                            activity.get().mEstablishments = new ArrayList<>();
                            activity.get().mEstablishments.addAll(page.getEstablishments());
                            activity.get().mMeta = page.getMeta();
                            Log.i("VIEW", "Meta is :" + page.getMeta());

                            if (!failed) {
                                activity.get().sharedPreferences.edit().putInt("pageNumber", pageNumber).apply();
                                activity.get().mViewPager = (ViewPager) activity.get().findViewById(R.id.container);
                                activity.get().mViewPager.setAdapter(activity.get().mSectionsPagerAdapter);

                                TabLayout tabLayout = (TabLayout) activity.get().findViewById(R.id.tabs);

                                activity.get().mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                                tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(activity.get().mViewPager));

                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            } else {
                                Log.e("DEBUG", "One of the request has failed");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            failed = true;
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            Snackbar.make(activity.get().findViewById(android.R.id.content), "Fail to request API", Snackbar.LENGTH_LONG).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(activity.get(), SearchActivity.class);
                                    activity.get().startActivity(intent);
                                }
                            }, 1000);
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("x-api-version", "2");
                    headers.put("accept", "application/json");
                    headers.put("content-type", "application/json");
                    return headers;
                }
            };
            EndPoint.getInstance(activity.get()).addToRequestQueue(getRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
