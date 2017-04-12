package com.example.bonuslib.identificator;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ivan on 4/12/17.
 */

// Singleton class for working identificator: get, set, delete
public class Identificator implements IdentificatorGetterInterface, IdentificatorSetterInterface {
    private static Identificator instance;
    private SharedPreferences sp;

    public synchronized static Identificator getInstance(Context spContext) {
        if (instance == null) {
            instance = new Identificator(spContext);
        }
        return instance;
    }

    private Identificator(Context spContext) {
        sp = spContext.getSharedPreferences(IdentificatorConstants.ID_SP_NAME, 0);
    }

    private Identificator(Context spContext, String id) {
        sp = spContext.getSharedPreferences(IdentificatorConstants.ID_SP_NAME, 0);
        if (id != null) {
            setId(id);
        }
    }

    @Override
    public String getId() {
        return sp.getString(IdentificatorConstants.ID_RECORD_NAME, null);
    }

    @Override
    public boolean setId(String id) {
        return sp.edit()
                .putString(IdentificatorConstants.ID_RECORD_NAME, id)
                .commit();
    }

    // should be called on logout
    public boolean clearId() {
        return sp.edit()
                .remove(IdentificatorConstants.ID_RECORD_NAME)
                .commit();
    }
}
