package com.myriding.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MysqlValue {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("rec_title")
    @Expose
    private String recTitle;
    @SerializedName("rec_time")
    @Expose
    private Integer recTime;
    @SerializedName("rec_distance")
    @Expose
    private Double recDistance;
    @SerializedName("rec_avg_speed")
    @Expose
    private Double recAvgSpeed;
    @SerializedName("rec_max_speed")
    @Expose
    private Double recMaxSpeed;
    @SerializedName("rec_start_point_address")
    @Expose
    private String recStartPointAddress;
    @SerializedName("rec_end_point_address")
    @Expose
    private String recEndPointAddress;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRecTitle() {
        return recTitle;
    }

    public void setRecTitle(String recTitle) {
        this.recTitle = recTitle;
    }

    public Integer getRecTime() {
        return recTime;
    }

    public void setRecTime(Integer recTime) {
        this.recTime = recTime;
    }

    public Double getRecDistance() {
        return recDistance;
    }

    public void setRecDistance(Double recDistance) {
        this.recDistance = recDistance;
    }

    public Double getRecAvgSpeed() {
        return recAvgSpeed;
    }

    public void setRecAvgSpeed(Double recAvgSpeed) {
        this.recAvgSpeed = recAvgSpeed;
    }

    public Double getRecMaxSpeed() {
        return recMaxSpeed;
    }

    public void setRecMaxSpeed(Double recMaxSpeed) {
        this.recMaxSpeed = recMaxSpeed;
    }

    public String getRecStartPointAddress() {
        return recStartPointAddress;
    }

    public void setRecStartPointAddress(String recStartPointAddress) {
        this.recStartPointAddress = recStartPointAddress;
    }

    public String getRecEndPointAddress() {
        return recEndPointAddress;
    }

    public void setRecEndPointAddress(String recEndPointAddress) {
        this.recEndPointAddress = recEndPointAddress;
    }
}
