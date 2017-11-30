package com.techpark.BonusHub.db.client_host;

import com.techpark.BonusHub.db.client.Client;
import com.techpark.BonusHub.db.host.Host;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Timur on 02-May-17.
 */

public class ClientHost {

    public final static String CLIENTHOST_CLIENT_FIELD_NAME = "client_id";
    public final static String CLIENTHOST_HOST_FIELD_NAME = "host_id";
    public final static String CLIENTHOST_POINTS_FIELD_NAME = "points";

    @DatabaseField(generatedId = true)
    private int Id;
    @DatabaseField(foreign = true, foreignAutoRefresh=true, columnName = CLIENTHOST_CLIENT_FIELD_NAME)
    private Client client;
    @DatabaseField(foreign = true, foreignAutoRefresh=true, columnName = CLIENTHOST_HOST_FIELD_NAME)
    private Host host;
    @DatabaseField(columnName = CLIENTHOST_POINTS_FIELD_NAME)
    private int points;

    public ClientHost() {
    }

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
