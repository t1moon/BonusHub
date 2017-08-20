package com.example.client.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    private RetrofitFactory() {
    }
//    192.168.31.122
    private final static Retrofit CLIENT_INSTANCE = new Retrofit.Builder()
            .baseUrl("http://http://t1moon.pythonanywhere.com/api/client/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public final static String MEDIA_URL = "media/";

    public static Retrofit retrofitClient() {
        return CLIENT_INSTANCE;
    }
}
