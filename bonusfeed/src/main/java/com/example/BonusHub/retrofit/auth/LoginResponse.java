package com.example.BonusHub.retrofit.auth;

/**
 * Created by mike on 16.04.17.
 */

public class LoginResponse {
    private int code;

    private boolean isHosted;

    private String user_id;

    private String host_id;

    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public boolean isHosted() {
        return isHosted;
    }

    public String getUserId() {
        return user_id;
    }

    public String getHostId() {
        return host_id;
    }
}
