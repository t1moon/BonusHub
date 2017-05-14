package com.example.BonusHub.activity.retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface ApiInterface {
    @POST("withdraw/")
    @Headers({
            "Accept: application/json"
    })
    Call<WithdrawResponse> withdraw(@Body Withdraw withdraw);

}
