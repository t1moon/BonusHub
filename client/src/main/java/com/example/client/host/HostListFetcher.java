package com.example.client.host;

import com.example.bonuslib.client.Client;
import com.example.bonuslib.host.Host;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;


interface HostListFetcher {
    @POST("list_hosts/")
    @Headers({
            "Accept: application/json"
    })
    Call<HostListResponse> listHosts(@Body int clientId);
}
