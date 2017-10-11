package com.example.BonusHub.retrofit;

import com.example.BonusHub.db.host.Host;
import com.example.BonusHub.retrofit.auth.Login;
import com.example.BonusHub.retrofit.auth.LoginResponse;
import com.example.BonusHub.retrofit.auth.LogoutResponse;
import com.example.BonusHub.retrofit.client.ClientResponse;
import com.example.BonusHub.retrofit.editInfo.EditResponse;
import com.example.BonusHub.retrofit.editInfo.UploadResponse;
import com.example.BonusHub.retrofit.getInfo.GetInfoResponse;
import com.example.BonusHub.retrofit.host.HostResult;
import com.example.BonusHub.retrofit.registration.RegistrationResult;
import com.example.BonusHub.retrofit.statistic.StatisticResponse;
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


public interface CommonApiInterface {
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
    Call<LogoutResponse> logout(@Header("Cookie") String cookie);

}
