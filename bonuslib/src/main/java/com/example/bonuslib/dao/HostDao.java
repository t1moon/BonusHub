package com.example.bonuslib.dao;

import android.database.SQLException;

import com.example.bonuslib.model.Host;
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

}
