package com.example.BonusHub.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    private RetrofitFactory() {
    }

    private final static Retrofit HOST_INSTANCE = new Retrofit.Builder()
            .baseUrl("http://195.19.44.158:11250/api/host/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public final static String MEDIA_URL = "media/";

    private final static Retrofit COMMON_INSTANCE = new Retrofit.Builder()
            .baseUrl("http://195.19.44.158:11250/api/user/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private final static Retrofit SCORE_INSTANCE = new Retrofit.Builder()
            .baseUrl("http://195.19.44.158:11250/api/score/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    public static Retrofit retrofitHost() {
        return HOST_INSTANCE;
    }

    public static Retrofit retrofitCommon() {
        return COMMON_INSTANCE;
    }

    public static Retrofit retrofitScore() {
        return SCORE_INSTANCE;
    }

}
