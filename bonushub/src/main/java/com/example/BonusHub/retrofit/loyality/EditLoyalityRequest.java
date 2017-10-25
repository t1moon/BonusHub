package com.example.BonusHub.retrofit.loyality;

/**
 * Created by ivan on 10/23/17.
 */

public class EditLoyalityRequest {
    private int loyality_type;
    private float loyality_param;
    private String offer;

    public EditLoyalityRequest(int loyality_type, float loyality_param, String offer) {
        this.loyality_type = loyality_type;
        this.loyality_param = loyality_param;
        this.offer = offer;
    }
}