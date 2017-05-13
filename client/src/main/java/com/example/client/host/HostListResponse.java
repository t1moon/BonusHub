package com.example.client.host;

/**
 * Created by Timur on 12-May-17.
 */
import com.google.gson.annotations.SerializedName;

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

        @SerializedName("title")
        private String title;

        @SerializedName("description")
        private String description;

        @SerializedName("address")
        private String address;

        // in minutes
        @SerializedName("time_open")
        private int time_open;

        // in minutes
        @SerializedName("time_close")
        private int time_close;

        @SerializedName("points")
        private int points;

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

        public int getTime_close() {
            return time_close;
        }

        public void setTime_close(int time_close) {
            this.time_close = time_close;
        }

        public int getTime_open() {
            return time_open;
        }

        public void setTime_open(int time_open) {
            this.time_open = time_open;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }
    }

}
