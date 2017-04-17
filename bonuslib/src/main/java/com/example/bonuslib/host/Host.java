package com.example.bonuslib.host;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Timur on 13-Apr-17.
 */
@DatabaseTable(tableName = "host")
public class Host{

    public final static String HOST_NAME_FIELD_NAME = "title";

    @DatabaseField(generatedId = true)
    private int Id;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = "title")
    private String title;

    @DatabaseField(columnName = "description")
    private String description;

    @DatabaseField(columnName = "address")
    private String address;

    @DatabaseField(columnName = "time_open", dataType = DataType.INTEGER)
    private int time_open;

    @DatabaseField(columnName = "time_close", dataType = DataType.INTEGER)
    private int time_close;


    public Host() {
    }

    public Host(String title, String description){
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTime_open() {
        return time_open;
    }

    public void setTime_open(int time_open) {
        this.time_open = time_open;
    }

    public int getTime_close() {
        return time_close;
    }

    public void setTime_close(int time_close) {
        this.time_close = time_close;
    }

    public int getId() {
        return Id;
    }
}
