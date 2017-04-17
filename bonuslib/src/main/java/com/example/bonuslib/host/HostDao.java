package com.example.bonuslib.host;

import android.database.SQLException;

import com.example.bonuslib.db.HelperFactory;
import com.example.bonuslib.host.Host;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.util.List;

/**
 * Created by Timur on 13-Apr-17.
 */

public class HostDao extends BaseDaoImpl<Host, Integer> {

    public HostDao(ConnectionSource connectionSource,
                   Class<Host> dataClass) throws SQLException, java.sql.SQLException {
        super(connectionSource, dataClass);
    }

    public List<Host> getAllHosts() throws SQLException, java.sql.SQLException {
        return this.queryForAll();
    }

    public Host getHostById(int id) throws java.sql.SQLException {
        return this.queryForId(id);
    }

    public int createHost(String title, String description,
                          String address, int open_hour, int open_minute,
                          int close_hour, int close_minute) throws java.sql.SQLException {
        Host host = new Host();
        host.setTitle(title);
        host.setDescription(description);
        host.setAddress(address);
        host.setTime_open(open_hour * 60 + open_minute);
        host.setTime_close(close_hour * 60 + close_minute);

        int host_id = -1;

        host_id = host.getId();
        this.create(host);
        return host_id;
    }

}
