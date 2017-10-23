package com.example.BonusHub.retrofit;

import com.example.BonusHub.retrofit.auth.LogoutResponse;
import com.example.BonusHub.retrofit.client.ClientResponse;
import com.example.BonusHub.retrofit.editInfo.EditResponse;
import com.example.BonusHub.retrofit.editInfo.UploadResponse;
import com.example.BonusHub.retrofit.getInfo.GetInfoResponse;
import com.example.BonusHub.retrofit.loyality.EditLoyalityRequest;
import com.example.BonusHub.retrofit.loyality.EditLoyalityResponse;
import com.example.BonusHub.retrofit.staff.GetStaffResponse;
import com.example.BonusHub.retrofit.staff.Hire;
import com.example.BonusHub.retrofit.staff.HireResponse;
import com.example.BonusHub.retrofit.staff.Retire;
import com.example.BonusHub.retrofit.staff.RetireResponse;
import com.example.BonusHub.retrofit.updatePoints.UpdatePointsPojo;
import com.example.BonusHub.retrofit.updatePoints.UpdatePointsResponse;
import com.example.BonusHub.retrofit.host.HostResult;
import com.example.BonusHub.retrofit.statistic.StatisticResponse;
import com.example.BonusHub.db.host.Host;

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
import retrofit2.http.Query;


public interface HostApiInterface {

    @POST("create/")
    @Headers({
            "Accept: application/json"
    })
    Call<HostResult> createHost(@Body Host host, @Header("Cookie")String cookie);

    @POST("edit_host/")
    @Headers({
            "Accept: application/json"
    })
    Call<EditResponse> editHost(@Body Host host, @Header("Cookie")String cookie);

    @POST("edit_loyality/")
    @Headers({
            "Accept: application/json"
    })
    Call<EditLoyalityResponse> editLoyality(@Body EditLoyalityRequest editLoyalityRequest,
                                            @Header("Cookie") String cookie);

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

    @POST("logout/")
    @Headers({
            "Accept: application/json"
    })
    Call<LogoutResponse> logout(@Header("Cookie")String cookie);

    @GET("get_staff/")
    @Headers({
            "Accept: application/json"
    })
    Call<GetStaffResponse> getStaff(@Header("Cookie")String cookie);

    @POST("hire/")
    @Headers({
            "Accept: application/json"
    })
    Call<HireResponse> hire(@Body Hire hire, @Header("Cookie")String cookie);


    @POST("retire/")
    @Headers({
            "Accept: application/json"
    })
    Call<RetireResponse> retire(@Body Retire retire, @Header("Cookie")String cookie);


}
