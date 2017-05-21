package com.example.BonusHub.activity.retrofit.updatePoints;

/**
 * Created by Timur on 13-May-17.
 */

public class UpdatePointsPojo {
    private int bill;
    private boolean is_add;
    private String client_identificator;


    public UpdatePointsPojo(String client_identificator, int bill, boolean isAdd) {
        this.bill = bill;
        this.is_add = isAdd;
        this.client_identificator = client_identificator;
    }
}
