package com.example.client.activity.api.login;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by mike on 07.05.17.
 */

public interface Logouter {
    @POST("client/logout/")
    @Headers({
            "Accept: application/json"
    })
    Call<LogoutResult> logout(@Header("Cookie") String cookie);
}
