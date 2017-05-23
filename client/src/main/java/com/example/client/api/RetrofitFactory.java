package com.example.client.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mike on 16.04.17.
 */

public class RetrofitFactory {

    private RetrofitFactory() {
    }

    private final static Retrofit CLIENT_INSTANCE = new Retrofit.Builder()
            .baseUrl("http://romvanocouponserver.pythonanywhere.com/api/client/")
            //.baseUrl("http://192.168.0.102:5000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static Retrofit retrofitClient() {
        return CLIENT_INSTANCE;
    }
}
