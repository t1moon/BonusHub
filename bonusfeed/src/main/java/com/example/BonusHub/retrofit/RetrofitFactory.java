package com.example.BonusHub.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    private RetrofitFactory() {
    }

//    private final static Retrofit HOST_INSTANCE = new Retrofit.Builder()
//            .baseUrl("http://195.19.44.158:11250/api/host/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build();
//    public final static String MEDIA_URL = "media/";
//
//    private final static Retrofit COMMON_INSTANCE = new Retrofit.Builder()
//            .baseUrl("http://195.19.44.158:11250/api/user/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build();
//
//    private final static Retrofit CLIENT_INSTANCE = new Retrofit.Builder()
//            .baseUrl("http://195.19.44.158:11250/api/client/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build();

    private final static Retrofit HOST_INSTANCE = new Retrofit.Builder()
            .baseUrl("http://192.168.0.104:5000/api/host/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public final static String MEDIA_URL = "media/";

    private final static Retrofit COMMON_INSTANCE = new Retrofit.Builder()
            .baseUrl("http://192.168.0.104:5000/api/user/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private final static Retrofit CLIENT_INSTANCE = new Retrofit.Builder()
            .baseUrl("http://192.168.0.104:5000/api/client/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    public static Retrofit retrofitHost() {
        return HOST_INSTANCE;
    }

    public static Retrofit retrofitCommon() {
        return COMMON_INSTANCE;
    }

    public static Retrofit retrofitClient() {
        return CLIENT_INSTANCE;
    }

}
