package com.example.BonusHub.activity.retrofit;

import com.example.BonusHub.activity.retrofit.auth.Login;
import com.example.BonusHub.activity.retrofit.auth.LoginResponse;
import com.example.BonusHub.activity.retrofit.auth.LogoutResponse;
import com.example.BonusHub.activity.retrofit.client.ClientResponse;
import com.example.BonusHub.activity.retrofit.clientapp.HostListResponse;
import com.example.BonusHub.activity.retrofit.registration.RegistrationResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ClientApiInterface {
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

    @GET("list_hosts/")
    @Headers({
            "Accept: application/json"
    })
    Call<HostListResponse> listHosts(@Header("Cookie")String cookie);

    @GET("get_info/")
    @Headers({
            "Accept: application/json"
    })
    Call<ClientResponse> getInfo(@Header("Cookie")String cookie);

    @POST("register/")
    @Headers({
            "Accept: application/json"
    })
    Call<RegistrationResult> registrate(@Body Login login);
}
