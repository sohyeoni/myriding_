package com.myriding.model;

public class BadgePreview {
    final static int MAX_NUM = 5;
    private String badgeType;
    private int numOfBadge;
    private int badgeImage;

    public BadgePreview(String badgeType, int numOfBadge, int badgeImage) {
        this.badgeType = badgeType;
        this.numOfBadge = numOfBadge;
        this.badgeImage = badgeImage;
    }

    public String getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(String badgeType) {
        this.badgeType = badgeType;
    }

    public int getNumOfBadge() {
        return numOfBadge;
    }

    public String getNumOfBadgeToString() {
        return numOfBadge + " / " + MAX_NUM;
    }

    public void setNumOfBadge(int numOfBadge) {
        this.numOfBadge = numOfBadge;
    }

    public int getBadgeImage() {
        return badgeImage;
    }

    public void setBadgeImage(int badgeImage) {
        this.badgeImage = badgeImage;
    }
}
