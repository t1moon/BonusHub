package com.example.client.activity.api.login;
import com.example.client.activity.api.Login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by mike on 16.04.17.
 */

public interface Loginner {
    @POST("client/login/")
    @Headers({
            "Accept: application/json"
    })
    Call<LoginResult> login(@Body Login login);
}
