package com.myriding.model;

import java.util.Date;

public class Record {
    Date date;
    String date2;
    double lat;
    double lng;
    double speed;

    public Record(Date date, double lat, double lng, double speed) {
        this.date = date;
        this.lat = lat;
        this.lng = lng;
        this.speed = speed;
    }

    public Record(String date, double lat, double lng, double speed) {
        this.date2 = date;
        this.lat = lat;
        this.lng = lng;
        this.speed = speed;
    }

    public Date getDate() {
        return date;
    }

    public String getDate2() {
        return date2;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public double getSpeed() {
        return speed;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
