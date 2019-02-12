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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {
    private final int FINE_LOCATION_PERMISSION = 1;
    private final LatLng UNITED_KINGDOM = new LatLng(52.412811, -1.778197);

    LocationManager locationManager;
    LocationListener locationListener;

    private ArrayList<Establishment> mEstablishments = new ArrayList<>();
    private HashMap<String, Integer> mMarkers = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.fragment_map_view, container, false);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
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
        getMyLocation();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        return view;
    }

    public void getMyLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (isLocationServiceEnabled()) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
    }

    public boolean isLocationServiceEnabled() {
        LocationManager locationManager = null;
        boolean gps_enabled = false, network_enabled = false;

        if (locationManager == null)
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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

    public void initialiseList(ArrayList<Establishment> establishments) {
        this.mEstablishments = establishments;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getMyLocation();
                }
                break;
        }
    }

    public void requestLocationPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION);
    }

    public LatLng getLatLng(Establishment e) {
        return new LatLng(e.getGeocode().getLatitude(), e.getGeocode().getLongitude());
    }

    public MarkerOptions createMarker(Establishment e) {
        LatLng position = getLatLng(e);
        String title = e.toString() + " - " + e.getRatingValue();
        String snippet = e.getBusinessType();
        float color;
        switch (e.getBusinessTypeID()) {
            case 1:                                                     // Restaurants
            case 7843:                                                     // Pub/Bar
            case 7844:
                color = BitmapDescriptorFactory.HUE_GREEN;
                break; // Takeaways
            case 7:
                color = BitmapDescriptorFactory.HUE_MAGENTA;
                break; // Distributors
            case 7838:
                color = BitmapDescriptorFactory.HUE_ORANGE;
                break; // Farmers
            case 5:
                color = BitmapDescriptorFactory.HUE_ROSE;
                break; // Hospitals
            case 7842:
                color = BitmapDescriptorFactory.HUE_AZURE;
                break; // Hotels
            case 14:                                                     // Import/Export
            case 7839:
                color = BitmapDescriptorFactory.HUE_VIOLET;
                break; // Manufactures
            case 7841:                                                     // Other Catering
            case 7846:
                color = BitmapDescriptorFactory.HUE_YELLOW;
                break; // Mobile Catering
            case 7840:                                                     // Retailers - Supermarket
            case 4613:
                color = BitmapDescriptorFactory.HUE_CYAN;
                break; // Retailers - Other
            case 7845:
                color = BitmapDescriptorFactory.HUE_BLUE;
                break; // Schools and University
            default:
                color = BitmapDescriptorFactory.HUE_RED;
                break; // ERROR
        }
        return new MarkerOptions()
                .position(position)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(color));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLngBounds mapBounds = null;
        //for (Establishment e : mEstablishments) {
        for (int i = 0; i < mEstablishments.size(); i++) {

            Establishment e = mEstablishments.get(i);
            if (e.getGeocode().getLongitude() == null || e.getGeocode().getLatitude() == null) {
                continue;
            }
            LatLng point = getLatLng(e);
            Log.i("DEBUG", "Est: " + e.toString() + " " + point.toString());
            if (mapBounds == null) {
                mapBounds = new LatLngBounds(point, point);
            } else {
                mapBounds = mapBounds.including(point);
            }
            Marker m = googleMap.addMarker(createMarker(e));
            mMarkers.put(m.getId(), e.getFHRSID());
        }

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getActivity(), EstablishmentDetail.class);
                intent.putExtra("establishmentID", mMarkers.get(marker.getId()));
                startActivity(intent);
            }
        });

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (isLocationServiceEnabled()) {
                googleMap.setMyLocationEnabled(true);
            }
        }

        CameraUpdate camera;
        if (mapBounds == null) {
            camera = CameraUpdateFactory.newLatLngZoom(UNITED_KINGDOM, 5);
        } else {
            camera = CameraUpdateFactory.newLatLngBounds(mapBounds, 200);
        }
        googleMap.animateCamera(camera, 1500, null);
    }

}
