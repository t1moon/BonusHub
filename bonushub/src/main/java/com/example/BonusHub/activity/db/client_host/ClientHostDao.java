package com.example.BonusHub.activity.db.client_host;

import android.database.SQLException;

import com.example.BonusHub.activity.db.client.Client;
import com.example.BonusHub.activity.db.host.Host;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.util.List;

/**
 * Created by Timur on 13-Apr-17.
 */

public class ClientHostDao extends BaseDaoImpl<ClientHost, Integer> {

    public ClientHostDao(ConnectionSource connectionSource,
                         Class<ClientHost> dataClass) throws SQLException, java.sql.SQLException {
        super(connectionSource, dataClass);
    }

    public List<ClientHost> lookupHostForClient(Client client) throws SQLException, java.sql.SQLException {
        List<ClientHost> clientHosts = this.queryBuilder().where().eq(ClientHost.CLIENTHOST_CLIENT_FIELD_NAME, client).query();
        return clientHosts;
    }

    public void createClientHost(Client client, Host host, int points) throws java.sql.SQLException {
        ClientHost clientHost= new ClientHost(client, host, points);
        this.create(clientHost);
    }

}
