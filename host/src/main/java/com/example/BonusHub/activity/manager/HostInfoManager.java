package com.example.BonusHub.activity.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.bonuslib.db.HelperFactory;
import com.example.bonuslib.host.Host;

import java.sql.SQLException;

/**
 * Created by Timur on 17-Apr-17.
 */

public class HostInfoManager implements HostManager {
    private HostInfoView hostInfoView;
    SharedPreferences sp;
    int host_id;
    Host host;

    public HostInfoManager(HostInfoView hostInfoView, Context context) {
        this.hostInfoView = hostInfoView;
        host_id = getHost_id(context);
    }
    @Override
    public void onResume() {

        }


    public void setHost_id(int host_id, Context context) {
        sp = context.getSharedPreferences("bonus", Context.MODE_PRIVATE);
        sp.edit().putInt("host_id", host_id).apply();
    }

    public int getHost_id(Context context) {
        sp = context.getSharedPreferences("bonus", Context.MODE_PRIVATE);
        return sp.getInt("host_id", 0);
    }
}
