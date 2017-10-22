package com.example.BonusHub.retrofit.updatePoints;

/**
 * Created by Timur on 13-May-17.
 */

public class UpdatePointsPojo {
    private Float score;
    private String user_id;


    public UpdatePointsPojo(String client_identificator, float bill) {
        this.score = bill;
        this.user_id = client_identificator;
    }
}
