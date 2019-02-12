package com.example.rinaldy.restauranthygienechecker;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Locale;

public class Utils {

    public static SharedPreferences.Editor putDouble(final SharedPreferences.Editor editor, final String key, final double value) {
        return editor.putLong(key, Double.doubleToRawLongBits(value));
    }

    public static double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public static int getRatingImage(Establishment e) {
        switch (e.getRatingValue()) {
            case "0":
                return R.drawable.fhrs_0;
            case "1":
                return R.drawable.fhrs_1;
            case "2":
                return R.drawable.fhrs_2;
            case "3":
                return R.drawable.fhrs_3;
            case "4":
                return R.drawable.fhrs_4;
            case "5":
                return R.drawable.fhrs_5;
            case "Awaiting Inspection":
                return R.drawable.fhrs_awaitinginspection;
            case "Awaiting Publication":
                return R.drawable.fhrs_awaitingpublication;
            case "Exempt":
            default:
                return R.drawable.fhrs_exempt;
        }
    }

    public static int getRatingIcon(Establishment e) {
        switch (e.getRatingValue()) {
            case "0":
                return R.drawable.rating_0;
            case "1":
                return R.drawable.rating_1;
            case "2":
                return R.drawable.rating_2;
            case "3":
                return R.drawable.rating_3;
            case "4":
                return R.drawable.rating_4;
            case "5":
                return R.drawable.rating_5;
            default:
                return R.drawable.rating_exempt;
        }
    }

    public static String getDistanceText(Establishment e) {
        if (e.getDistance() == null) return "";
        return (e.getDistance() < 0.1 ? "< 0.1" : String.format(Locale.getDefault(), "%.2f", e.getDistance())) + " miles";
    }

    public static String check(String s) {
        return s != null && !s.isEmpty() ? s : "";
    }

    public static String checkURI(String attr, String s) {
        return s != null && !s.isEmpty() ? "&" + attr + "=" + s : "";
    }

    public static String checkSpinner(String attr, Spinner s) {
        return s.getSelectedItem() == null ? "" : checkURI(attr, s.getSelectedItem().toString());
    }

    public static int getBusinessTypeID(ArrayAdapter<BusinessTypes.BusinessType> adapter, String element) {
        for(int i = 0; i < adapter.getCount(); i++) {
            BusinessTypes.BusinessType now = adapter.getItem(i);
            if(element.equals(now.getBusinessTypeName())) {
                return now.getBusinessTypeId();
            }
        }
        return -1;
    }

    public static int getLocalAuthorityID(ArrayAdapter<Authorities.Authority> adapter, String element) {
        for(int i = 0; i < adapter.getCount(); i++) {
            Authorities.Authority now = adapter.getItem(i);
            if(element.equals(now.getName())) {
                return now.getLocalAuthorityId();
            }
        }
        return -1;
    }


}
