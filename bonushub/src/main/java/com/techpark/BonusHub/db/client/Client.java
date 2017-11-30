package com.techpark.BonusHub.db.client;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Timur on 13-Apr-17.
 */
@DatabaseTable(tableName = "client")
public class Client {

    public final static String CLIENT_ID_FIELD_NAME = "client_id";
    public final static String CLIENT_NAME_FIELD_NAME = "name";
    public final static String CLIENT_IDENTIFICATOR_FIELD_NAME = "identificator";
    @DatabaseField(generatedId = true, columnName = CLIENT_ID_FIELD_NAME)
    private int Id;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = CLIENT_NAME_FIELD_NAME)
    private String name;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = CLIENT_IDENTIFICATOR_FIELD_NAME)
    private String identificator;

    public Client() {
    }

    public Client(String title, String description){
        this.name = title;
        this.identificator = description;
    }

    public int getId() {
        return Id;
    }

    public String getIdentificator() {
        return identificator;
    }

    public void setIdentificator(String identificator) {
        this.identificator = identificator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
