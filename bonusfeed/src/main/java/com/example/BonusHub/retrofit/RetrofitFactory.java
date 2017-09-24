package com.example.BonusHub.retrofit;

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

    private final static Retrofit CLIENT_INSTANCE = new Retrofit.Builder()
            .baseUrl("http://t1moon.pythonanywhere.com/api/client/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static Retrofit retrofitHost() {
        return HOST_INSTANCE;
    }

    public static Retrofit retrofitClient() {
        return CLIENT_INSTANCE;
    }

}
