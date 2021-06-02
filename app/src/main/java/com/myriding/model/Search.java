package com.myriding.model;

import java.util.Date;

public class Search {
    private int     id;
    private int     courseUserId;
    private String  courseName;
    private String  courseImage;
    private double  courseDistance;
    private int     courseTime;
    private int     courseLike;
    private Date    date;
    private String  startPoint;
    private String  endPoint;
    private int     tryCount;

    public Search(int id, int courseUserId, String courseName, String courseImage, double courseDistance,
                  int courseTime, int courseLike, Date date, String startPoint, String endPoint, int tryCount) {
        this.id             = id;
        this.courseUserId   = courseUserId;
        this.courseName     = courseName;
        this.courseImage    = courseImage;
        this.courseDistance = courseDistance;
        this.courseTime     = courseTime;
        this.courseLike     = courseLike;
        this.date           = date;
        this.startPoint     = startPoint;
        this.endPoint       = endPoint;
        this.tryCount       = tryCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseUserId() {
        return courseUserId;
    }

    public void setCourseUserId(int courseUserId) {
        this.courseUserId = courseUserId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseImage() {
        return courseImage;
    }

    public void setCourseImage(String courseImage) {
        this.courseImage = courseImage;
    }

    public double getCourseDistance() {
        return courseDistance;
    }

    public void setCourseDistance(double courseDistance) {
        this.courseDistance = courseDistance;
    }

    public int getCourseTime() {
        return courseTime;
    }

    public void setCourseTime(int courseTime) {
        this.courseTime = courseTime;
    }

    public int getCourseLike() {
        return courseLike;
    }

    public void setCourseLike(int courseLike) {
        this.courseLike = courseLike;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public int getTryCount() {
        return tryCount;
    }

    public void setTryCount(int tryCount) {
        this.tryCount = tryCount;
    }
}
