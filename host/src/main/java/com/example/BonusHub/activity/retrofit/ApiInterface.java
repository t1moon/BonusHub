package com.example.BonusHub.activity.retrofit;

import com.example.BonusHub.activity.retrofit.editInfo.EditPojo;
import com.example.BonusHub.activity.retrofit.editInfo.EditResponse;
import com.example.BonusHub.activity.retrofit.editInfo.UploadPojo;
import com.example.BonusHub.activity.retrofit.editInfo.UploadResponse;
import com.example.BonusHub.activity.retrofit.getInfo.GetInfoResponse;
import com.example.BonusHub.activity.retrofit.statistic.StatisticResponse;
import com.example.BonusHub.activity.retrofit.updatePoints.UpdatePointsPojo;
import com.example.BonusHub.activity.retrofit.updatePoints.UpdatePointsResponse;
import com.example.bonuslib.host.Host;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;


public interface ApiInterface {
    @POST("update_points/")
    @Headers({
            "Accept: application/json"
    })
    Call<UpdatePointsResponse> update_points(@Body UpdatePointsPojo updatePointsPojo, @Header("Cookie")String cookie);

    @POST("edit_host/")
    @Headers({
            "Accept: application/json"
    })
    Call<EditResponse> editHost(@Body Host host, @Header("Cookie")String cookie);

    @Multipart
    @POST("upload/")
    Call<UploadResponse> upload(
            @Part MultipartBody.Part image,
            @Header("Cookie")String cookie
    );

    @GET("get_client/{identificator}/")
    @Headers({
            "Accept: application/json"
    })
    Call<ClientResponse> getPoints(@Path("identificator") String identificator, @Header("Cookie")String cookie);

    @GET("statistic/")
    @Headers({
            "Accept: application/json"
    })
    Call<StatisticResponse> getStatistic(@Header("Cookie")String cookie);

    @GET("info/")
    @Headers({
            "Accept: application/json"
    })
    Call<GetInfoResponse> getInfo(@Header("Cookie")String cookie);



}
