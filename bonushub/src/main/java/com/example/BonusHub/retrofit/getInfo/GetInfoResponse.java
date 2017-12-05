package com.example.BonusHub.retrofit.getInfo;

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
    private String time_open;
    @SerializedName("time_close")
    private String time_close;
    @SerializedName("profile_image")
    private String profile_image;
    @SerializedName("loyality_param")
    private Float loyality_param;
    @SerializedName("loyality_type")
    private int loyality_type;
    @SerializedName("loyality_burn_param")
    private int loyality_burn;
    @SerializedName("loyality_time_param")
    private int loyality_time;

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

    public String getTime_open() {
        return time_open;
    }

    public void setTime_open(String time_open) {
        this.time_open = time_open;
    }

    public String getTime_close() {
        return time_close;
    }

    public void setTime_close(String time_close) {
        this.time_close = time_close;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public void setLoyalityType(int loy_prog) {
        loyality_type = loy_prog;
    }

    public void setLoyalityParam(float loy_param) {
        loyality_param = loy_param;
    }

    public void setLoyality_burn(int loyality_burn) {
        this.loyality_burn = loyality_burn;
    }

    public void setLoyality_time(int loyality_time) {
        this.loyality_time = loyality_time;
    }

    public int getLoyalityType() {
        return loyality_type;
    }

    public float getLoyalityParam() {
        return loyality_param;
    }

    public int getLoyality_burn() {
        return loyality_burn;
    }

    public int getLoyality_time() {
        return loyality_time;
    }
}
