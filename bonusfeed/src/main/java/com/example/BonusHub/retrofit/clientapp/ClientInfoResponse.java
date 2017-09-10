package com.example.BonusHub.retrofit.clientapp;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Timur on 23-May-17.
 */

public class ClientInfoResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("name")
    private String name;

    @SerializedName("identificator")
    private String identificator;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentificator() {
        return identificator;
    }

    public void setIdentificator(String identificator) {
        this.identificator = identificator;
    }
}
