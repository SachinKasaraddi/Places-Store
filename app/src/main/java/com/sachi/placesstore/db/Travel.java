package com.sachi.placesstore.db;

import io.realm.RealmObject;

/**
 * Created by sachin.kasaraddi on 27/07/16.
 */
public class Travel extends RealmObject{
    private String latitude;
    private String longitude;
    private String title;
    private String address;
    private String details;

    public Travel() {
    }

    public Travel(String latitude, String longitude, String title, String address, String details) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.address = address;
        this.details = details;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

}
