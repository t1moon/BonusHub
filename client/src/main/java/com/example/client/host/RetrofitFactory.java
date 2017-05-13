package com.example.client.host;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class RetrofitFactory {

    private RetrofitFactory() {
    }

    private final static Retrofit CLIENT_INSTANCE = new Retrofit.Builder()
            .baseUrl("http://10.0.3.2:5000/api/client/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public final static String MEDIA_URL = "media/";

    static Retrofit retrofitClient() {
        return CLIENT_INSTANCE;
    }
}
