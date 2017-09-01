package com.example.BonusHub.activity.retrofit.updatePoints;

/**
 * Created by Timur on 12-May-17.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdatePointsResponse {

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
