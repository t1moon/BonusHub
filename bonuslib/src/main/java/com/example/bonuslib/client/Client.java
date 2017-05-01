package com.example.bonuslib.client;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Timur on 13-Apr-17.
 */
@DatabaseTable(tableName = "client")
public class Client {

    @DatabaseField(generatedId = true)
    private int Id;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = "title")
    private String name;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = "identificator")
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
