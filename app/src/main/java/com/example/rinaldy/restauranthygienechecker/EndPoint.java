package com.example.rinaldy.restauranthygienechecker;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

/**
 * End point parser class for convenience.
 * <p>
 * We need calls for:
 * - Business
 * - Business type
 * - Region
 * - Authority
 * - Distance
 * - Ratings
 * - Sort Options?
 */
public class EndPoint {

    public static String URLEstablishments = "http://api.ratings.food.gov.uk/establishments";
    public static String URLBusinessTypes = "http://api.ratings.food.gov.uk/businessTypes";
    public static String URLRegions = "http://api.ratings.food.gov.uk/regions";
    public static String URLAuthorities = "http://api.ratings.food.gov.uk/authorities";
    public static String URLRatings = "http://api.ratings.food.gov.uk/ratings";

    public static String schemeTypeKey = "&schemeTypeKey=FHRS";
    public static String sortByDistance = "&sortOptionKey=distance";
    public static String sortByRelevance = "&sortOptionKey=relevance";

    public static int pageSize = 30;

    public static EndPoint mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private EndPoint(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized EndPoint getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new EndPoint(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

    public static String URLEstablishmentByID(Integer id) {
        return URLEstablishments + "/" + id;
    }

    public static String URLEstablishmentByName(String name, String place) {
        name = name.trim();
        place = place.trim();

        String URL = URLEstablishments + "?" + schemeTypeKey + sortByRelevance;
        if (!name.isEmpty()) {
            URL += "&name=" + name.replace(" ", "%20");
        }
        if (!place.isEmpty()) {
            URL += "&address=" + place.replace(" ", "%20");
        }
        return URL;
    }

    public static String URLEstablishmentsByLocation(double longitude, double latitude) {
        return URLEstablishments
                + "?longitude=" + longitude
                + "&latitude=" + latitude
                + sortByDistance
                + schemeTypeKey;

    }

    public static String URLPaging(int pageNumber) {
        return "&pageSize=" + pageSize
                + "&pageNumber=" + pageNumber;

    }
}
