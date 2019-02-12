package com.example.rinaldy.restauranthygienechecker;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AdvancedSearchActivity extends AppCompatActivity {
    private final int FINE_LOCATION_PERMISSION = 1;

    EditText mName;
    EditText mPlace;
    EditText mRange;
    Spinner mBusinessTypeSpinner;
    Spinner mRegionSpinner;
    Spinner mAuthoritySpinner;
    Spinner mRatingSpinner;
    Spinner mRatingCompSpinner;
    Switch mNearMeSwitch;
    TextView mPlaceLabel;
    TextView mRegionLabel;
    TextView mAuthorityLabel;
    TextView mRangeLabel;
    TextView mRangeMiles;

    LocationManager locationManager;
    LocationListener locationListener;

    private double longitude;
    private double latitude;
    private int pageNumber = 1;

    private ArrayList<BusinessTypes.BusinessType> mBusinessTypes;
    private ArrayAdapter mBusinessTypesAdapter;

    private ArrayList<Regions.Region> mRegions;
    private ArrayAdapter mRegionsAdapter;

    private ArrayList<Authorities.Authority> mAuthorities;
    private ArrayAdapter mAuthoritiesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        if (getSupportActionBar() != null) {
            String title = "Advanced Search";
            SpannableString s = new SpannableString(title);
            s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getSupportActionBar().setTitle(s);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        this.mName = (EditText) findViewById(R.id.adv_search_name);
        this.mPlace = (EditText) findViewById(R.id.adv_search_place);
        this.mRange = (EditText) findViewById(R.id.adv_search_range);
        this.mBusinessTypeSpinner = (Spinner) findViewById(R.id.adv_search_type);
        this.mRegionSpinner = (Spinner) findViewById(R.id.adv_search_region);
        this.mAuthoritySpinner = (Spinner) findViewById(R.id.adv_search_authority);
        this.mRatingSpinner = (Spinner) findViewById(R.id.adv_search_rating);
        this.mRatingCompSpinner = (Spinner) findViewById(R.id.adv_search_rating_comp);
        this.mNearMeSwitch = (Switch) findViewById(R.id.adv_search_near_me);
        this.mPlaceLabel = (TextView) findViewById(R.id.adv_search_label_place);
        this.mRegionLabel = (TextView) findViewById(R.id.adv_search_label_region);
        this.mAuthorityLabel = (TextView) findViewById(R.id.adv_search_label_authority);
        this.mRangeLabel = (TextView) findViewById(R.id.adv_search_label_range);
        this.mRangeMiles = (TextView) findViewById(R.id.adv_search_label_miles);

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
        this.pageNumber = getIntent().getIntExtra("pageNumber", 1);
        mNearMeSwitch.setOnClickListener(nearMeSwitchListener); // also checks if location is enabled.

        initialiseBusinessType();
        initialiseRegions();
        initialiseAuthorities();

        mRegionSpinner.setOnItemSelectedListener(regionOnItemClick);

        Button clearBtn = (Button) findViewById(R.id.adv_search_clear_btn);
        clearBtn.setOnClickListener(clearBtnOnClick);

        Button searchBtn = (Button) findViewById(R.id.adv_search_search_btn);
        searchBtn.setOnClickListener(searchBtnOnClick);
    }

    public void initialiseBusinessType() {
        mBusinessTypes = new ArrayList<>();
        StringRequest getRequest = new StringRequest(Request.Method.GET, EndPoint.URLBusinessTypes,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        BusinessTypes result = GsonUtils.getInstance().getGson().fromJson(response, BusinessTypes.class);
                        mBusinessTypes.addAll(Arrays.asList(result.getBusinessTypes()));
                        mBusinessTypesAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(findViewById(android.R.id.content), "Fail to request BusinessType API", Snackbar.LENGTH_LONG).show();
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

        mBusinessTypesAdapter = new ArrayAdapter<>(AdvancedSearchActivity.this, android.R.layout.simple_spinner_dropdown_item, mBusinessTypes);
        mBusinessTypeSpinner.setAdapter(mBusinessTypesAdapter);
        EndPoint.getInstance(this).addToRequestQueue(getRequest);
    }

    public void initialiseRegions() {
        mRegions = new ArrayList<>();
        mRegions.add(0, new Regions.Region(-1, "All", "-1"));
        StringRequest getRequest = new StringRequest(Request.Method.GET, EndPoint.URLRegions,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Regions result = GsonUtils.getInstance().getGson().fromJson(response, Regions.class);
                        mRegions.addAll(Arrays.asList(result.getRegions()));
                        mRegionsAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(findViewById(android.R.id.content), "Fail to request Region API", Snackbar.LENGTH_LONG).show();
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

        mRegionsAdapter = new ArrayAdapter<>(AdvancedSearchActivity.this, android.R.layout.simple_spinner_dropdown_item, mRegions);
        mRegionSpinner.setAdapter(mRegionsAdapter);
        EndPoint.getInstance(this).addToRequestQueue(getRequest);
    }

    public void initialiseAuthorities() {
        mAuthorities = new ArrayList<>();
        mAuthorities.add(0, new Authorities.Authority(-1, "-1", "All", "N/A"));
        StringRequest getRequest = new StringRequest(Request.Method.GET, EndPoint.URLAuthorities,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Authorities result = GsonUtils.getInstance().getGson().fromJson(response, Authorities.class);
                        mAuthorities.addAll(Arrays.asList(result.getAuthorities()));
                        mAuthoritiesAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(findViewById(android.R.id.content), "Fail to request Authority API", Snackbar.LENGTH_LONG).show();
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

        mAuthoritiesAdapter = new ArrayAdapter<>(AdvancedSearchActivity.this, android.R.layout.simple_spinner_dropdown_item, mAuthorities);
        mAuthoritySpinner.setAdapter(mAuthoritiesAdapter);
        EndPoint.getInstance(this).addToRequestQueue(getRequest);
    }

    public void filterAuthoritiesByRegion(String region) {
        ArrayList<Authorities.Authority> filteredList = new ArrayList<>();
        for (Authorities.Authority a : mAuthorities) {
            if (a.getRegionName().equals(region)) {
                filteredList.add(a);
            }
        }

        mAuthoritiesAdapter = new ArrayAdapter<>(AdvancedSearchActivity.this, android.R.layout.simple_spinner_dropdown_item, filteredList);
        mAuthoritySpinner.setAdapter(mAuthoritiesAdapter);
        mAuthoritiesAdapter.notifyDataSetChanged();
    }

    public void restoreAuthorities() {
        mAuthoritiesAdapter = new ArrayAdapter<>(AdvancedSearchActivity.this, android.R.layout.simple_spinner_dropdown_item, mAuthorities);
        mAuthoritySpinner.setAdapter(mAuthoritiesAdapter);
        mAuthoritiesAdapter.notifyDataSetChanged();
    }

    public String getURI() {
        String URI = EndPoint.URLEstablishments + "?";
        URI += Utils.checkURI("name", mName.getText().toString());
        int bId = Utils.getBusinessTypeID(mBusinessTypesAdapter, mBusinessTypeSpinner.getSelectedItem().toString());
        URI += mBusinessTypeSpinner.getSelectedItem() != null && bId > 0 ? "&businessTypeId=" + bId : "";
        if (mNearMeSwitch.isChecked()) {
            URI += Utils.checkURI("longitude", String.valueOf(longitude));
            URI += Utils.checkURI("latitude", String.valueOf(latitude));
            URI += Utils.checkURI("maxDistanceLimit", mRange.getText().toString());
            URI += EndPoint.sortByDistance;
        } else {
            URI += Utils.checkURI("address", mPlace.getText().toString());
            int aId = Utils.getLocalAuthorityID(mAuthoritiesAdapter, mAuthoritySpinner.getSelectedItem().toString());
            URI += mAuthoritySpinner.getSelectedItem() != null && aId > 0 ? "&localAuthorityId=" + aId : "";
        }
        URI += Utils.checkSpinner("ratingKey", mRatingSpinner);
        URI += Utils.checkSpinner("ratingOperatorKey", mRatingCompSpinner);
        Log.i("DEBUG", URI);
        return URI;
    }

    final View.OnClickListener clearBtnOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mName.setText("");
            mPlace.setText("");
            mRange.setText("");
            mBusinessTypeSpinner.setSelection(0);
            mRegionSpinner.setSelection(0);
            mAuthoritySpinner.setSelection(0);
            mRatingSpinner.setSelection(0);
            mRatingCompSpinner.setSelection(0);
        }
    };

    final View.OnClickListener searchBtnOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(AdvancedSearchActivity.this, ViewActivity.class);
            getPreferences(Context.MODE_PRIVATE).edit().putInt("pageNumber", 1).apply();
            intent.putExtra("title", "Advanced Search");
            intent.putExtra("search", getURI());
            intent.putExtra("firstOpen", true);
            startActivity(intent);
        }
    };

    final AdapterView.OnItemSelectedListener regionOnItemClick = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            String selectedRegion = mRegionSpinner.getSelectedItem().toString();
            if (!selectedRegion.equals("All")) {
                filterAuthoritiesByRegion(selectedRegion);
            } else {
                restoreAuthorities();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
        }
    };

    final View.OnClickListener nearMeSwitchListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (mNearMeSwitch.isChecked()) {
                if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    requestLocationPermissions();
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();
                    }

                    actionOnLocationEnabled();
                }
            } else {
                toggleSwitches(false);
            }
        }
    };

    public void actionOnLocationEnabled() {
        if (isLocationServiceEnabled()) {
            toggleSwitches(true);
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
            mNearMeSwitch.setChecked(false);
        }
    }

    public void toggleSwitches(boolean on) {
        if (on) {
            mPlace.setVisibility(View.GONE);
            mPlace.setText("");
            mPlaceLabel.setVisibility(View.GONE);
            mRegionSpinner.setVisibility(View.GONE);
            mRegionSpinner.setSelection(0);
            mRegionLabel.setVisibility(View.GONE);
            mAuthoritySpinner.setVisibility(View.GONE);
            mRegionSpinner.setSelection(0);
            mAuthorityLabel.setVisibility(View.GONE);

            mRange.setVisibility(View.VISIBLE);
            mRangeLabel.setVisibility(View.VISIBLE);
            mRangeMiles.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Now searching from device location.",Toast.LENGTH_SHORT).show();
        } else {
            mRange.setVisibility(View.GONE);
            mRange.setText("");
            mRangeLabel.setVisibility(View.GONE);
            mRangeMiles.setVisibility(View.GONE);

            mPlace.setVisibility(View.VISIBLE);
            mPlaceLabel.setVisibility(View.VISIBLE);
            mRegionSpinner.setVisibility(View.VISIBLE);
            mRegionLabel.setVisibility(View.VISIBLE);
            mAuthoritySpinner.setVisibility(View.VISIBLE);
            mAuthorityLabel.setVisibility(View.VISIBLE);
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
                                            mNearMeSwitch.setChecked(false);
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
                Intent intent = new Intent(AdvancedSearchActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
