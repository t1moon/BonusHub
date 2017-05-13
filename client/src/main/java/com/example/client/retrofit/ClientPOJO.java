package com.example.client.retrofit;

/**
 * Created by Timur on 13-May-17.
 */

public class ClientPOJO {
    private int client_id;

    public ClientPOJO(int client_id) {
        this.client_id = client_id;
    }
    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }
}
