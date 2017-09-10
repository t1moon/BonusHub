package com.example.BonusHub.retrofit.editInfo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Timur on 19-May-17.
 */

public class EditResponse {
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
