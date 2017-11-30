package com.techpark.client.retrofit.login;

/**
 * Created by mike on 16.04.17.
 */

public class LoginResult {
    private int code;

    private boolean isHosted;

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
}
