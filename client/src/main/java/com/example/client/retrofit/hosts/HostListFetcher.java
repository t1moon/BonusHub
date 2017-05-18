package com.example.client.retrofit.hosts;

import com.example.client.retrofit.ClientPOJO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface HostListFetcher {
    @POST("list_hosts/")
    @Headers({
            "Accept: application/json"
    })
    Call<HostListResponse> listHosts(@Body ClientPOJO clientPOJO);
}
