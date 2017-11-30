package com.techpark.BonusHub.retrofit.auth;

/**
 * Created by mike on 16.04.17.
 */

public class LoginResponse {
    private int code;

    private String message;

    private String user_id;

    private String host_id;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getUserId() {
        return user_id;
    }

    public String getHostId() {
        return host_id;
    }


}
