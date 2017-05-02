package com.example.bonuslib.client_host;

import com.example.bonuslib.client.Client;
import com.example.bonuslib.host.Host;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Timur on 02-May-17.
 */

public class ClientHost {

    @DatabaseField(generatedId = true)
    private int Id;
    @DatabaseField(foreign = true, columnName = "client_id")
    private Client client;
    @DatabaseField(foreign = true, columnName = "host_id")
    private Host host;
    @DatabaseField(canBeNull = true, columnName = "points")
    private int points;

    public ClientHost(Client client, Host host, int points) {
        this.client = client;
        this.host= host;
        this.points = points;
    }
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
