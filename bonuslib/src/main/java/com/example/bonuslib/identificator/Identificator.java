package com.example.bonuslib.identificator;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ivan on 4/12/17.
 */

// Singleton class for working identificator: get, set, delete
@Singleton
public class Identificator implements IdentificatorGetterInterface, IdentificatorSetterInterface {
    private static Identificator instance = new Identificator(spContext);
    private SharedPreferences sp;

    public synchronized static Identificator getInstance(Context spContext) {
        return instance;
    }

    private Identificator(Context spContext, String id=null) {
        sp = spContext.getSharedPreferences();
        if (id != null) {
            setId(id);
        }
    }

    @Override
    String getId() {
        return sp.getString(IdentificatorConstants.ID_RECORD_NAME, null);
    }

    @Override
    boolean setId(String id) {
        return sp.edit()
                .putString(IdentificatorConstants.ID_RECORD_NAME, id)
                .commit();
    }

    @OnLogout
    boolean clearId() {
        return sp.edit()
                .remove(IdentificatorConstants.ID_RECORD_NAME)
                .commit();
    }
}
