package com.example.BonusHub.retrofit;

import com.example.BonusHub.retrofit.updatePoints.UpdatePointsPojo;
import com.example.BonusHub.retrofit.updatePoints.UpdatePointsResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;


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
