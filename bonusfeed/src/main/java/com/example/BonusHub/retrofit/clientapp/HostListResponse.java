package com.example.BonusHub.retrofit.clientapp;

/**
 * Created by Timur on 12-May-17.
 */
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.util.List;

public class HostListResponse {

    @SerializedName("code")
    private int code;

    @SerializedName("hosts")
    private List<HostPoints> hosts;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<HostPoints> getHosts() {
        return hosts;
    }

    public void setHosts(List<HostPoints> hosts) {
        this.hosts = hosts;
    }


    public class HostPoints {

        @SerializedName("host_id")
        private String host_id;

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

        @SerializedName("points")
        private float points;

        @SerializedName("loyality_type")
        private int loyality_type;

        @SerializedName("loyality_param")
        private float loyality_param;

        @SerializedName("longitude")
        private double longitude;

        @SerializedName("latitude")
        private double latitude;

        @SerializedName("offer")
        private String offer;

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

        public String getTime_close() {
            return time_close;
        }

        public String getTime_open() {
            return time_open;
        }

        public void setTime_close(String time_close) {
            this.time_close = time_close;
        }

        public void setTime_open(String time_open) {
            this.time_open = time_open;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getPoints() {
            return (int)Math.floor(points);
        }

        public void setPoints(int points) {
            this.points = points;
        }

        public String getProfile_image() {
            return profile_image;
        }

        public void setProfile_image(String profile_image) {
            this.profile_image = profile_image;
        }

        public int getLoyalityType() {
            return loyality_type;
        }

        public void setLoyalityType(int type) {
            this.loyality_type = type ;
        }

        public float getLoyalityParam() {
            return loyality_param;
        }

        public void setLoyalityParam(float param) {
            this.loyality_param = param ;
        }

        public void setLongitude(double longt) {
            longitude = longt;
        }

        public void setLatitude(double lat) {
            latitude = lat;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setOffer(String newOffer) {
            offer = newOffer;
        }

        public String getOffer() {
            return offer;
        }
    }

}
