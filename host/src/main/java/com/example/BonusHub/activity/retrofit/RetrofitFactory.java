package com.example.BonusHub.activity.retrofit;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    private RetrofitFactory() {
    }

//    192.168.31.122
    private final static Retrofit HOST_INSTANCE = new Retrofit.Builder()
            .baseUrl("http://romvanocouponserver.pythonanywhere.com/api/host/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public final static String MEDIA_URL = "media/";

    public static Retrofit retrofitHost() {
        return HOST_INSTANCE;
    }
}
