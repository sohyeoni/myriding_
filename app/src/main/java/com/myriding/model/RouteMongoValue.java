package com.myriding.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RouteMongoValue {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
