package com.techpark.BonusHub.retrofit;

import com.techpark.BonusHub.retrofit.auth.Login;
import com.techpark.BonusHub.retrofit.auth.LoginResponse;
import com.techpark.BonusHub.retrofit.auth.LogoutResponse;
import com.techpark.BonusHub.retrofit.registration.RegistrationResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;


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
