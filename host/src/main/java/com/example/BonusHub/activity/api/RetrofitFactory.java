package com.example.BonusHub.activity.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mike on 16.04.17.
 */

public class RetrofitFactory {

    private RetrofitFactory() {
    }

    private final static Retrofit BARMEN_INSTANCE = new Retrofit.Builder()
            .baseUrl("http://romvanocouponserver.pythonanywhere.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static Retrofit retrofitBarmen() {
        return BARMEN_INSTANCE;
    }
}
