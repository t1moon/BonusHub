package com.example.BonusHub.retrofit.staff;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timur on 18-May-17.
 */

public class GetStaffResponse {

    @SerializedName("code")
    private int code;
    @SerializedName("staff")
    private List<Staff> staff;

    public List<Staff> getStaff() {
        return staff;
    }

    public void setStaff(List<Staff> staff) {
        this.staff = staff;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public class Staff {
        @SerializedName("login")
        private String login;
        @SerializedName("worker_id")
        private String worker_id;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getWorker_id() {
            return worker_id;
        }

        public void setWorker_id(String worker_id) {
            this.worker_id = worker_id;
        }
    }
}
