package com.example.BonusHub.activity.retrofit;

/**
 * Created by Timur on 13-May-17.
 */

public class Withdraw {
    private int host_id;
    private int points;
    private String client_identificator;


    public Withdraw(int host_id, int points, String client_identificator) {
        this.host_id = host_id;
        this.points = points;
        this.client_identificator = client_identificator;
    }
}
