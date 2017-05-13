package com.example.client.host;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class RetrofitFactory {

    private RetrofitFactory() {
    }

    private final static Retrofit CLINET_INSTANCE = new Retrofit.Builder()
            .baseUrl("http://10.0.3.2:5000/api/client/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    static Retrofit retrofitClient() {
        return CLINET_INSTANCE;
    }
}
