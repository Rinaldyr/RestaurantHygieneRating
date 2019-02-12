package com.example.rinaldy.restauranthygienechecker;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class EstablishmentDetail extends AppCompatActivity {

    private Integer FHRSID;
    private Establishment establishment;

    private TextView businessName;
    private TextView businessType;
    private TextView dateAwarded;
    private ImageView rating;

    private TextView address;
    private TextView postCode;
    private Button showOnMap;
    private Button getDirections;

    private TextView authorityName;
    private TextView authorityEmail;
    private TextView authorityWebsite;
    private Button emailBtn;
    private Button websiteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment_detail);

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        businessName = (TextView) findViewById(R.id.establishment_item_name);
        businessType = (TextView) findViewById(R.id.establishment_type);
        dateAwarded = (TextView) findViewById(R.id.date);
        rating = (ImageView) findViewById(R.id.establishment_item_rating);

        address = (TextView) findViewById(R.id.address);
        postCode = (TextView) findViewById(R.id.postcode);
        showOnMap = (Button) findViewById(R.id.show_on_map_btn);
        getDirections = (Button) findViewById(R.id.get_directions_btn);

        authorityName = (TextView) findViewById(R.id.authority_name);
        authorityEmail = (TextView) findViewById(R.id.authority_email);
        authorityWebsite = (TextView) findViewById(R.id.authority_website);
        emailBtn = (Button) findViewById(R.id.email_btn);
        websiteBtn = (Button) findViewById(R.id.website_btn);

        FHRSID = getIntent().getIntExtra("establishmentID", -1);
        initialisePage();
    }

    public void initialisePage() {
        String url = EndPoint.URLEstablishmentByID(FHRSID);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        establishment = GsonUtils.getInstance().getGson().fromJson(response, Establishment.class);

                        businessName.setText(Utils.check(establishment.getBusinessName()));
                        businessType.setText(Utils.check(establishment.getBusinessType()));
                        String date = "Date Awarded: " + (establishment.getHumanlyDate().isEmpty() ? "Not Specified" : establishment.getHumanlyDate());
                        dateAwarded.setText(date);
                        rating.setBackgroundResource(Utils.getRatingImage(establishment));

                        address.setText(String.format("%s", establishment.getFullAddress("\n")));
                        postCode.setText(Utils.check(establishment.getPostCode()));

                        Double lat = establishment.getGeocode().getLatitude();
                        Double lon = establishment.getGeocode().getLongitude();
                        if (lat != null && lon != null) {
                            getDirections.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Double lat = establishment.getGeocode().getLatitude();
                                    Double lon = establishment.getGeocode().getLongitude();
                                    if (lat != null && lon != null) {
                                        String uri = "geo:<" + lat + ">,<" + lon + ">?q=<" + lat + ">,<" + lon + ">(" + establishment.getBusinessName() + ")";
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(view.getContext(), "Please try again.", Toast.LENGTH_SHORT);
                                    }
                                }
                            });

                            showOnMap.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Double lat = establishment.getGeocode().getLatitude();
                                    Double lon = establishment.getGeocode().getLongitude();
                                    if (lat != null && lon != null) {
                                        Intent intent = new Intent(EstablishmentDetail.this, DetailMapActivity.class);
                                        intent.putExtra("est_name", establishment.getBusinessName());
                                        intent.putExtra("est_longitude", lon);
                                        intent.putExtra("est_latitude", lat);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(view.getContext(), "Please try again.", Toast.LENGTH_SHORT);
                                    }
                                }
                            });
                        } else {
                            getDirections.setVisibility(View.GONE);
                            showOnMap.setVisibility(View.GONE);
                        }

                        authorityName.setText(Utils.check(establishment.getLocalAuthorityName()));
                        if (establishment.getLocalAuthorityEmailAddress() != null && !establishment.getLocalAuthorityEmailAddress().isEmpty()) {
                            emailBtn.setVisibility(View.VISIBLE);
                            emailBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String email = establishment.getLocalAuthorityEmailAddress();
                                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
                                    startActivity(Intent.createChooser(intent, "Send email..."));
                                }
                            });
                        }
                        authorityEmail.setText(Utils.check(establishment.getLocalAuthorityEmailAddress()));
                        if (establishment.getLocalAuthorityWebsite() != null && !establishment.getLocalAuthorityWebsite().isEmpty()) {
                            websiteBtn.setVisibility(View.VISIBLE);
                            websiteBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String url = establishment.getLocalAuthorityWebsite();
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.setData(Uri.parse(url));
                                    startActivity(intent);
                                }
                            });
                        }
                        authorityWebsite.setText(Utils.check(establishment.getLocalAuthorityWebsite()));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(findViewById(android.R.id.content), "Fail to request item API", Snackbar.LENGTH_LONG).show();
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

        EndPoint.getInstance(this).addToRequestQueue(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Restaurant Hygiene Checker");
                shareIntent.putExtra(Intent.EXTRA_TEXT, establishment + " has got a rating of " + establishment.getRatingValue() + "!");
                startActivity(Intent.createChooser(shareIntent, "Share via..."));
                return true;
            case R.id.about:
                Intent intent = new Intent(EstablishmentDetail.this, AboutActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);

        }
    }



}
