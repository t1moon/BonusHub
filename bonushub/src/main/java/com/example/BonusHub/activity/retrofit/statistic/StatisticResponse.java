package com.example.BonusHub.activity.retrofit.statistic;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.types.DateTimeType;

import java.util.List;

/**
 * Created by Timur on 17-May-17.
 */

public class StatisticResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("response")
    private List<Operation> operationList;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Operation> getOperationList() {
        return operationList;
    }

    public void setOperationList(List<Operation> operationList) {
        this.operationList = operationList;
    }

    public class Operation {

        @SerializedName("date")
        private String date;

        @SerializedName("avg_bill")
        private float avgBill;

        @SerializedName("income")
        private int income;

        @SerializedName("outcome")
        private int outcome;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getIncome() {
            return income;
        }

        public void setIncome(int income) {
            this.income = income;
        }

        public int getOutcome() {
            return outcome;
        }

        public void setOutcome(int outcome) {
            this.outcome = outcome;
        }

        public float getAvgBill() {
            return avgBill;
        }

        public void setAvgBill(float avgBill) {
            this.avgBill = avgBill;
        }
    }
}
