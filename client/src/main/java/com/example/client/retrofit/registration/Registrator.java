package com.example.client.retrofit.registration;
import com.example.client.retrofit.Login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by mike on 07.05.17.
 */

public interface Registrator {
    @POST("register/")
    @Headers({
            "Accept: application/json"
    })
    Call<RegistrationResult> registrate(@Body Login login);
}
