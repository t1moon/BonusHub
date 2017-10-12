package com.example.BonusHub.retrofit.staff;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Timur on 18-May-17.
 */

public class RetireResponse {

    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
