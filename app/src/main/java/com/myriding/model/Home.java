package com.myriding.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Home {
    private String mTitle;
    private String mStartPoint;
    private String mEndPoint;
    private double mDistance;
    private int mTime;
    private double mAvgSpeed;
    private double mMaxSpeed;
    private ArrayList<LatLng> arrayPoints;

    public Home(String title, String startPoint, String endPoint,
                double distance, int time, double avgSpeed, double maxSpeed) {
        mTitle      = title;
        mStartPoint = startPoint;
        mEndPoint   = endPoint;
        mDistance   = distance;
        mTime       = time;
        mAvgSpeed   = avgSpeed;
        mMaxSpeed   = maxSpeed;
    }

    // <-- Getter
    public String getTitle() {
        return mTitle;
    }

    public String getStartPoint() {
        return mStartPoint;
    }

    public String getEndPoint() {
        return mEndPoint;
    }

    public double getDistance() {
        return mDistance;
    }

    public String getTime() {
        String time = "";
        int tempTime = mTime / 60;

        if(tempTime > 0) {
            time = (mTime / 60) + "시간 ";
        }

        time += (mTime % 60) + "분";

        return time;
    }

    public double getAvgSpeed() {
        return mAvgSpeed;
    }

    public double getMaxSpeed() {
        return mMaxSpeed;
    }

    public ArrayList<LatLng> getArrayPoints() {
        return arrayPoints;
    }

    // -->

    // <-- Setter
    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setStartPoint(String mStartPoint) {
        this.mStartPoint = mStartPoint;
    }

    public void setEndPoint(String mEndPoint) {
        this.mEndPoint = mEndPoint;
    }

    public void setDistance(double mDistance) {
        this.mDistance = mDistance;
    }

    public void setTime(int mTime) {
        this.mTime = mTime;
    }

    public void setAvgSpeed(double mAvgSpeed) {
        this.mAvgSpeed = mAvgSpeed;
    }

    public void setMaxSpeed(double mMaxSpeed) {
        this.mMaxSpeed = mMaxSpeed;
    }

    public void setArrayPoints(ArrayList<LatLng> arrayPoints) {
        this.arrayPoints = arrayPoints;
    }
    // -->
}
