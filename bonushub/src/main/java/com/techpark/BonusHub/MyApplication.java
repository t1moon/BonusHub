package com.techpark.BonusHub;

import android.app.Application;

import com.techpark.BonusHub.db.HelperFactory;

/**
 * Created by Timur on 13-Apr-17.
 */


public class MyApplication extends Application {
    private static MyApplication mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        HelperFactory.setHelper(getApplicationContext());
        mInstance = this;
    }

    @Override
    public void onTerminate() {
        HelperFactory.releaseHelper();
        super.onTerminate();
    }
    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

}

