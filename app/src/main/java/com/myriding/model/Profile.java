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
    @SerializedName("user_num_of_riding")
    @Expose
    private int userNumOfRiding;
    @SerializedName("stat")
    @Expose
    private List<Stat> stat = null;
    @SerializedName("badge")
    @Expose
    private BadgeCount badge = null;

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

    public int getUserNumOfRiding() {
        return userNumOfRiding;
    }

    public void setUserNumOfRiding(int userNumOfRiding) {
        this.userNumOfRiding = userNumOfRiding;
    }

    public List<Stat> getStat() {
        return stat;
    }

    public void setStat(List<Stat> stat) {
        this.stat = stat;
    }

    public BadgeCount getBadge() {
        return badge;
    }

    public void setBadge(BadgeCount badge) {
        this.badge = badge;
    }
}
