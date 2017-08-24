package com.example.BonusHub.activity.retrofit;

import com.example.BonusHub.activity.retrofit.client.ClientResponse;
import com.example.BonusHub.activity.retrofit.auth.Login;
import com.example.BonusHub.activity.retrofit.editInfo.EditResponse;
import com.example.BonusHub.activity.retrofit.editInfo.UploadResponse;
import com.example.BonusHub.activity.retrofit.getInfo.GetInfoResponse;
import com.example.BonusHub.activity.retrofit.auth.LoginResponse;
import com.example.BonusHub.activity.retrofit.auth.LogoutResponse;
import com.example.BonusHub.activity.retrofit.host.HostResult;
import com.example.BonusHub.activity.retrofit.registration.RegistrationResult;
import com.example.BonusHub.activity.retrofit.statistic.StatisticResponse;
import com.example.BonusHub.activity.retrofit.updatePoints.UpdatePointsPojo;
import com.example.BonusHub.activity.retrofit.updatePoints.UpdatePointsResponse;
import com.example.bonuslib.host.Host;

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


public interface HostApiInterface {
    @POST("register/")
    @Headers({
            "Accept: application/json"
    })
    Call<RegistrationResult> registrate(@Body Login login);

    @POST("login/")
    @Headers({
            "Accept: application/json"
    })
    Call<LoginResponse> login(@Body Login login);

    @POST("logout/")
    @Headers({
            "Accept: application/json"
    })
    Call<LogoutResponse> logout(@Header("Cookie")String cookie);

    @POST("create/")
    @Headers({
            "Accept: application/json"
    })
    Call<HostResult> createHost(@Body Host host, @Header("Cookie")String cookie);

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
