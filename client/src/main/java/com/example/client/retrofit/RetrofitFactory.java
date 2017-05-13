package com.example.client.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    private RetrofitFactory() {
    }

    private final static Retrofit CLIENT_INSTANCE = new Retrofit.Builder()
            .baseUrl("http://10.0.3.2:5000/api/client/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public final static String MEDIA_URL = "media/";

    public static Retrofit retrofitClient() {
        return CLIENT_INSTANCE;
    }
}
