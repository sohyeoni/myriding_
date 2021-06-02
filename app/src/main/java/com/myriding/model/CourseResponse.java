package com.myriding.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CourseResponse {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("routes")
    @Expose
    private List<CourseData> routes;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<CourseData> getRoutes() {
        return routes;
    }

    public void setRoutes(List<CourseData> routes) {
        this.routes = routes;
    }
}
