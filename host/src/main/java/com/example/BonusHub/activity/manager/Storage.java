package com.example.BonusHub.activity.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Timur on 17-Apr-17.
 */

public class Storage {
    static SharedPreferences sp;

    public static void setHost_id(int host_id, Context context) {
        sp = context.getSharedPreferences("bonus", Context.MODE_PRIVATE);
        sp.edit().putInt("host_id", host_id).apply();
    }

    public int getHost_id(Context context) {
        sp = context.getSharedPreferences("bonus", Context.MODE_PRIVATE);
        return sp.getInt("host_id", 0);
    }

}
