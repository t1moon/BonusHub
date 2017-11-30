package com.techpark.client.retrofit.client;

import com.techpark.client.retrofit.hosts.HostListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

/**
 * Created by Timur on 23-May-17.
 */

public interface ClientInfoFetcher {
    @GET("get_info/")
    @Headers({
            "Accept: application/json"
    })
    Call<ClientResponse> getInfo(@Header("Cookie")String cookie);
}
