package com.myriding.model;

public class PopularCourse {
    private String img;
    private int id;
    private String title;
    private double distance;
    private int like;

    public PopularCourse(int id, String title, double distance, int like, String img) {
        this.id = id;
        this.title = title;
        this.distance = distance;
        this.like = like;
        this.img = img;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }
}
