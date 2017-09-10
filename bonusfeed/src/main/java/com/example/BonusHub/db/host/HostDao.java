package com.example.BonusHub.db.host;

import android.database.SQLException;

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

    public int createHost(Host host) throws java.sql.SQLException {
        this.create(host);
        int host_id = host.getId();
        return host_id;
    }

}
