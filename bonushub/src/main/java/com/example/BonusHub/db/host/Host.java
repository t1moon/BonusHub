package com.example.BonusHub.db.host;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Timur on 13-Apr-17.
 */
@DatabaseTable(tableName = "host")
public class Host{

    public final static String HOST_ID_FIELD_NAME = "host_id";
    public final static String HOST_TITLE_FIELD_NAME = "title";
    public final static String HOST_DESCRIPTION_FIELD_NAME = "description";
    public final static String HOST_ADDRESS_FIELD_NAME = "address";
    public final static String HOST_TIMEOPEN_FIELD_NAME = "time_open";
    public final static String HOST_TIMECLOSE_FIELD_NAME = "time_close";
    public final static String HOST_IMAGE_FIELD_NAME = "profile_image";

    @DatabaseField(generatedId = true, columnName = HOST_ID_FIELD_NAME)
    private int Id;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = HOST_TITLE_FIELD_NAME)
    private String title;

    @DatabaseField(columnName = HOST_DESCRIPTION_FIELD_NAME)
    private String description;

    @DatabaseField(columnName = HOST_ADDRESS_FIELD_NAME)
    private String address;

    // in minutes
    @DatabaseField(columnName = HOST_TIMEOPEN_FIELD_NAME, dataType = DataType.INTEGER)
    private int time_open;

    // in minutes
    @DatabaseField(columnName = HOST_TIMECLOSE_FIELD_NAME, dataType = DataType.INTEGER)
    private int time_close;

    @DatabaseField(columnName = HOST_IMAGE_FIELD_NAME, dataType = DataType.STRING)
    private String profile_image;


    public Host() {
    }

    public Host(String title, String description, String address){
        this.title = title;
        this.description = description;
        this.address = address;
    }

    public Host(String title, String description, String address, int time_open, int time_close){
        this.title = title;
        this.description = description;
        this.address = address;
        this.time_open = time_open;
        this.time_close = time_close;
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

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}
