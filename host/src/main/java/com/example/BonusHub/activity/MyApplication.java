package com.example.BonusHub.activity;

import android.app.Application;

import com.example.bonuslib.db.HelperFactory;

/**
 * Created by Timur on 13-Apr-17.
 */


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HelperFactory.setHelper(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        HelperFactory.releaseHelper();
        super.onTerminate();
    }
}

