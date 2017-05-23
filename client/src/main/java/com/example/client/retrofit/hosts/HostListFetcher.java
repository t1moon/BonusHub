package com.example.client.retrofit.hosts;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface HostListFetcher {
    @GET("list_hosts/")
    @Headers({
            "Accept: application/json"
    })
    Call<HostListResponse> listHosts(@Header("Cookie")String cookie);
}
