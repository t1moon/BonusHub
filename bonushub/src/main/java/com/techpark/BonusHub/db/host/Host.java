package com.techpark.BonusHub.db.host;

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
    public final static String HOST_LONG = "longitude";
    public final static String HOST_LAT = "latitude";
//    public final static String HOST_LOYALITY_PROGRAMM = "loyality_programm";
//    public final static String HOST_LOYALITY_PARAM = "loyality_param";

    @DatabaseField(generatedId = true, columnName = HOST_ID_FIELD_NAME)
    private int Id;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = HOST_TITLE_FIELD_NAME)
    private String title;

    @DatabaseField(columnName = HOST_DESCRIPTION_FIELD_NAME)
    private String description;

    @DatabaseField(columnName = HOST_ADDRESS_FIELD_NAME)
    private String address;

    // in minutes
    @DatabaseField(columnName = HOST_TIMEOPEN_FIELD_NAME, dataType = DataType.STRING)
    private String time_open;

    // in minutes
    @DatabaseField(columnName = HOST_TIMECLOSE_FIELD_NAME, dataType = DataType.STRING)
    private String time_close;

    @DatabaseField(columnName = HOST_IMAGE_FIELD_NAME, dataType = DataType.STRING)
    private String profile_image;

    @DatabaseField(columnName = HOST_LONG, dataType = DataType.DOUBLE)
    private double longitude;

    @DatabaseField(columnName = HOST_LAT, dataType = DataType.DOUBLE)
    private double latitude;

//    @DatabaseField(columnName = HOST_LOYALITY_PROGRAMM, dataType = DataType.INTEGER)
//    private Integer loyality_type;
//
//    @DatabaseField(columnName = HOST_LOYALITY_PARAM, dataType = DataType.double)
//    private double loyality_param;

    public Host() {
    }

    public Host(String title, String description, String address){
        this.title = title;
        this.description = description;
        this.address = address;
    }

    public Host(String title, String description, String address, String time_open, String time_close){
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

    public String getTime_open() {
        return time_open;
    }

    public void setTime_open(String time_open) {
        this.time_open = time_open;
    }

    public String getTime_close() {
        return time_close;
    }

    public void setTime_close(String time_close) {
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

//    public void setLoyalityType(int loy_prog) {
//        loyality_type = loy_prog;
//    }
//
//    public void setLoyalityParam(double loy_param) {
//        loyality_param = loy_param;
//    }
//
//    public int getLoyalityType() {
//        return loyality_type;
//    }
//
//    public double getLoyalityParam() {
//        return loyality_param;
//    }

    public void setLongitude(double longt) {
        longitude = longt;
    }

    public void setLatitude(double lat) {
        latitude = lat;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
