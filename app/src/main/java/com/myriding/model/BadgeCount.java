package com.myriding.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BadgeCount {
    @SerializedName("timeBadge")
    @Expose
    private int timeBadge;
    @SerializedName("distanceBadge")
    @Expose
    private int distanceBadge;
    @SerializedName("maxSpeedBadge")
    @Expose
    private int maxSpeedBadge;

    public int getTimeBadge() {
        return timeBadge;
    }

    public void setTimeBadge(int timeBadge) {
        this.timeBadge = timeBadge;
    }

    public int getDistanceBadge() {
        return distanceBadge;
    }

    public void setDistanceBadge(int distanceBadge) {
        this.distanceBadge = distanceBadge;
    }

    public int getMaxSpeedBadge() {
        return maxSpeedBadge;
    }

    public void setMaxSpeedBadge(int maxSpeedBadge) {
        this.maxSpeedBadge = maxSpeedBadge;
    }
}
