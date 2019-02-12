package com.example.rinaldy.restauranthygienechecker;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String name;
    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_map);

        name = getIntent().getStringExtra("est_name");
        longitude = getIntent().getDoubleExtra("est_longitude", 0.0);
        latitude = getIntent().getDoubleExtra("est_latitude", 0.0);

        if (getSupportActionBar() != null){
            SpannableString s = new SpannableString(name);
            s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getSupportActionBar().setTitle(s);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.detailMapView);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng here = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(here).title(name));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(here, 15));
    }
}
