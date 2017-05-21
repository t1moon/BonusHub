package com.example.BonusHub.activity.api.host;

import com.example.bonuslib.host.Host;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by mike on 15.05.17.
 */

public interface Hoster {
    @POST("barmen/create/")
    @Headers({
            "Accept: application/json"
    })
    Call<HostResult> login(@Body Host host, @Header("Cookie")String cookie);
}