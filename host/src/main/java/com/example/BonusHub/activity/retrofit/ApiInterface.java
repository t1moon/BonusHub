package com.example.BonusHub.activity.retrofit;

import com.example.BonusHub.activity.retrofit.editInfo.EditPojo;
import com.example.BonusHub.activity.retrofit.editInfo.EditResponse;
import com.example.BonusHub.activity.retrofit.getInfo.GetInfoResponse;
import com.example.BonusHub.activity.retrofit.statistic.StatisticResponse;
import com.example.BonusHub.activity.retrofit.updatePoints.UpdatePointsPojo;
import com.example.BonusHub.activity.retrofit.updatePoints.UpdatePointsResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface ApiInterface {
    @POST("update_points/")
    @Headers({
            "Accept: application/json"
    })
    Call<UpdatePointsResponse> update_points(@Body UpdatePointsPojo updatePointsPojo);

    @POST("edit_host/")
    @Headers({
            "Accept: application/json"
    })
    Call<EditResponse> editHost(@Body EditPojo editPojo);


    @GET("{host_id}/get_client/{identificator}/")
    @Headers({
            "Accept: application/json"
    })
    Call<ClientResponse> getPoints(@Path("host_id") int host_id, @Path("identificator") String identificator);

    @GET("{host_id}/statistic/")
    @Headers({
            "Accept: application/json"
    })
    Call<StatisticResponse> getStatistic(@Path("host_id") int host_id);

    @GET("{host_id}/info/")
    @Headers({
            "Accept: application/json"
    })
    Call<GetInfoResponse> getInfo(@Path("host_id") int host_id);


}
