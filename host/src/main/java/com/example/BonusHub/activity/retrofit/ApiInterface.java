package com.example.BonusHub.activity.retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface ApiInterface {
    @POST("update_points/")
    @Headers({
            "Accept: application/json"
    })
    Call<UpdatePointsResponse> update_points(@Body UpdatePointsPojo updatePointsPojo,@Header("Cookie")String cookie);

    @GET("{host_id}/get_client/{identificator}/")
    @Headers({
            "Accept: application/json"
    })
    Call<ClientResponse> getPoints(@Path("host_id") int host_id, @Path("identificator") String identificator);


}
