package com.myriding.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RankData {
    @SerializedName("picture")
    @Expose
    private String picture;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("nickname")
    @Expose
    private String nickname;
    @SerializedName("score")
    @Expose
    private int score;
    @SerializedName("time")
    @Expose
    private int time;
    @SerializedName("distance")
    @Expose
    private double distance;
    @SerializedName("max_speed")
    @Expose
    private double maxSpeed;
    @SerializedName("avg_speed")
    @Expose
    private double avgSpeed;

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }
}
