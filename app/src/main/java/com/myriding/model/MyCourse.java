package com.myriding.model;

import java.util.Date;

public class MyCourse {
    private String img;
    private int id;
    private int routeUserId;
    private String title;
    private String startPoint;
    private String endPoint;
    private Date date;
    private double distance;
    private int time;
    private int like;

    public MyCourse(String img, int id, int routeUserId, String title, String startPoint, String endPoint, Date date, double distance, int time, int like) {
        this.img = img;
        this.id = id;
        this.routeUserId = routeUserId;
        this.title = title;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.date = date;
        this.distance = distance;
        this.time = time;
        this.like = like;
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

    public int getRouteUserId() {
        return routeUserId;
    }

    public void setRouteUserId(int routeUserId) {
        this.routeUserId = routeUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }
}
