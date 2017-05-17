package com.example.BonusHub.activity.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    private RetrofitFactory() {
    }

    private final static Retrofit HOST_INSTANCE = new Retrofit.Builder()
            .baseUrl("http://10.0.3.2:5000/api/host/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    public static Retrofit retrofitHost() {
        return HOST_INSTANCE;
    }
}
