package com.example.rinaldy.restauranthygienechecker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;

public class GsonUtils {
    private static GsonUtils mInstance;
    private Gson gson;

    private GsonUtils() {
         gson = new GsonBuilder()
                 .enableComplexMapKeySerialization()
                 .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                 .create();
    }

    public static GsonUtils getInstance() {
        if (mInstance == null) {
            mInstance = new GsonUtils();
        }
        return mInstance;
    }

    public String toJson(Object object) {
        return gson.toJson(object);
    }

    public Gson getGson() {
        return gson;
    }
}
