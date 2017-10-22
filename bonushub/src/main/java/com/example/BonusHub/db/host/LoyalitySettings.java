package com.example.BonusHub.db.host;

import com.example.BonusHub.Location;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by mike on 23.10.17.
 */

public class LoyalitySettings {
    public final static String HOST_LOYALITY_PROGRAMM = "loyality_programm";
    public final static String HOST_LOYALITY_PARAM = "loyality_param";

    public LoyalitySettings(Integer prog, Float param) {
        loyality_param = param;
        loyality_programm = prog;
    }

    @DatabaseField(columnName = HOST_LOYALITY_PROGRAMM, dataType = DataType.INTEGER)
    private Integer loyality_programm;

    @DatabaseField(columnName = HOST_LOYALITY_PARAM, dataType = DataType.FLOAT)
    private Float loyality_param;

    public void setLoyalityProgramm(int loy_prog) {
        loyality_programm = loy_prog;
    }

    public void setLoyalityParam(float loy_param) {
        loyality_param = loy_param;
    }

    public int getLoyalityProgramm() {
        return loyality_programm;
    }

    public float getLoyalityParam() {
        return loyality_param;
    }
}
