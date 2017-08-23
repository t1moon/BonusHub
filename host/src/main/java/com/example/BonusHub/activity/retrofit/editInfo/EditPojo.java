package com.example.BonusHub.activity.retrofit.editInfo;

import com.example.bonuslib.host.Host;

/**
 * Created by Timur on 19-May-17.
 */

public class EditPojo {
    private int host_id;
    private Host host;

    public EditPojo(int host_id, Host host) {
        this.host_id = host_id;
        this.host = host;
    }
}
