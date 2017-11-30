package com.techpark.BonusHub.retrofit;

import com.techpark.BonusHub.retrofit.updatePoints.UpdatePointsPojo;
import com.techpark.BonusHub.retrofit.updatePoints.UpdatePointsResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface ScoreApiInterface {

    @POST("cup/")
    @Headers({
            "Accept: application/json"
    })
    Call<UpdatePointsResponse> updateCups(@Body UpdatePointsPojo updatePointsPojo, @Header("Cookie") String cookie);

    @POST("percent/")
    @Headers({
            "Accept: application/json"
    })
    Call<UpdatePointsResponse> updateBonus(@Body UpdatePointsPojo updatePointsPojo, @Header("Cookie") String cookie);
}
