package com.myriding.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Profile {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("user_nickname")
    @Expose
    private String userNickname;
    @SerializedName("user_picture")
    @Expose
    private String userPicture;
    @SerializedName("user_score_of_riding")
    @Expose
    private int userScoreOfRiding;
    @SerializedName("stat")
    @Expose
    private List<Stat> stat = null;
    @SerializedName("badge")
    @Expose
    private List<Badge> badge = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

    public int getUserScoreOfRiding() {
        return userScoreOfRiding;
    }

    public void setUserScoreOfRiding(int userScoreOfRiding) {
        this.userScoreOfRiding = userScoreOfRiding;
    }

    public List<Stat> getStat() {
        return stat;
    }

    public void setStat(List<Stat> stat) {
        this.stat = stat;
    }

    public List<Badge> getBadge() {
        return badge;
    }

    public void setBadge(List<Badge> badge) {
        this.badge = badge;
    }
}
