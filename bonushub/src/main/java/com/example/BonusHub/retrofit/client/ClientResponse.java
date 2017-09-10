package com.example.BonusHub.retrofit.client;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Timur on 14-May-17.
 */
public class ClientResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("points")
    private int points;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
