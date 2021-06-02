package com.myriding.model;

public class Rank {
    private int myRankNum;
    private String img;
    private int id;
    private String nickname;
    private int score;
    private double distance;
    private int time;
    private double avgSpeed;
    private double maxSpeed;

    public Rank(int myRankNum, String img, int id, String nickname, int score) {
        this.myRankNum = myRankNum;
        this.img = img;
        this.id = id;
        this.nickname = nickname;
        this.score = score;
    }

    public int getMyRankNum() {
        return myRankNum;
    }

    public void setMyRankNum(int myRankNum) {
        this.myRankNum = myRankNum;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
}
