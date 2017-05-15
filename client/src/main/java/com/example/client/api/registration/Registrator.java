package com.example.client.api.registration;
import com.example.client.api.Login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by mike on 07.05.17.
 */

public interface Registrator {
    @POST("client/register/")
    @Headers({
            "Accept: application/json"
    })
    Call<RegistrationResult> registrate(@Body Login login);
}
