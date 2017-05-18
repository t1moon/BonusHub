package com.example.BonusHub.activity.retrofit.getInfo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Timur on 18-May-17.
 */

public class GetInfoResponse {

    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("address")
    private String address;
    @SerializedName("time_open")
    private int time_open;
    @SerializedName("time_close")
    private int time_close;
    @SerializedName("profile_image")
    private String profile_image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTime_open() {
        return time_open;
    }

    public void setTime_open(int time_open) {
        this.time_open = time_open;
    }

    public int getTime_close() {
        return time_close;
    }

    public void setTime_close(int time_close) {
        this.time_close = time_close;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}
