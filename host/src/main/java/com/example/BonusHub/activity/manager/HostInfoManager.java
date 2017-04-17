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
        try {
            host = HelperFactory.getHelper().getHostDAO().getHostById(host_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (host != null) {
            String title = host.getTitle();
            String description = host.getDescription();
            String address = host.getAddress();
            int open_hour = host.getTime_open() / 60;
            int open_minute = host.getTime_open() % 60;
            int close_hour = host.getTime_close() / 60;
            int close_minute = host.getTime_close() % 60;
            if (hostInfoView != null)
                hostInfoView.setInfo(title, description, address, open_hour, open_minute, close_hour, close_minute);
        }
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
