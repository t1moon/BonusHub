package com.example.bonuslib.client;

import android.database.SQLException;

import com.example.bonuslib.host.Host;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.util.List;

/**
 * Created by Timur on 13-Apr-17.
 */

public class ClientDao extends BaseDaoImpl<Client, Integer> {

    public ClientDao(ConnectionSource connectionSource,
                     Class<Client> dataClass) throws SQLException, java.sql.SQLException {
        super(connectionSource, dataClass);
    }

    public List<Client> getAllClients() throws SQLException, java.sql.SQLException {
        return this.queryForAll();
    }

    public Client getClientById(int id) throws java.sql.SQLException {
        return this.queryForId(id);
    }

    public int createClient(String name, String identificator) throws java.sql.SQLException {
        Client client = new Client(name, identificator);
        this.create(client);
        int client_id = client.getId();
        return client_id;
    }

}
