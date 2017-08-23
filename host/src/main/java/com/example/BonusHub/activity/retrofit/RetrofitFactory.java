package com.example.BonusHub.activity.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    private RetrofitFactory() {
    }

    private final static Retrofit HOST_INSTANCE = new Retrofit.Builder()
            .baseUrl("http://t1moon.pythonanywhere.com/api/host/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public final static String MEDIA_URL = "media/";

    public static Retrofit retrofitHost() {
        return HOST_INSTANCE;
    }
}
