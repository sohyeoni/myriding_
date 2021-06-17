package com.myriding.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RouteValue {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("route_user_id")
    @Expose
    private Integer routeUserId;
    @SerializedName("route_title")
    @Expose
    private String routeTitle;
    @SerializedName("route_image")
    @Expose
    private String routeImage;
    @SerializedName("route_distance")
    @Expose
    private double routeDistance;
    @SerializedName("route_time")
    @Expose
    private Integer routeTime;
    @SerializedName("route_like")
    @Expose
    private Integer routeLike;
    @SerializedName("route_num_of_try_count")
    @Expose
    private Integer routeNumOfTryCount;
    @SerializedName("route_num_of_try_user")
    @Expose
    private Integer routeNumOfTryUser;
    @SerializedName("route_start_point_address")
    @Expose
    private String routeStartPointAddress;
    @SerializedName("route_end_point_address")
    @Expose
    private String routeEndPointAddress;
    @SerializedName("route_avg_degree")
    @Expose
    private double routeAvgDegree;
    @SerializedName("route_max_altitude")
    @Expose
    private double routeMaxAltitude;
    @SerializedName("route_min_altitude")
    @Expose
    private double routeMinAltitude;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRouteUserId() {
        return routeUserId;
    }

    public void setRouteUserId(Integer routeUserId) {
        this.routeUserId = routeUserId;
    }

    public String getRouteTitle() {
        return routeTitle;
    }

    public void setRouteTitle(String routeTitle) {
        this.routeTitle = routeTitle;
    }

    public String getRouteImage() {
        return routeImage;
    }

    public void setRouteImage(String routeImage) {
        this.routeImage = routeImage;
    }

    public double getRouteDistance() {
        return routeDistance;
    }

    public void setRouteDistance(double routeDistance) {
        this.routeDistance = routeDistance;
    }

    public Integer getRouteTime() {
        return routeTime;
    }

    public void setRouteTime(Integer routeTime) {
        this.routeTime = routeTime;
    }

    public Integer getRouteLike() {
        return routeLike;
    }

    public void setRouteLike(Integer routeLike) {
        this.routeLike = routeLike;
    }

    public Integer getRouteNumOfTryCount() {
        return routeNumOfTryCount;
    }

    public void setRouteNumOfTryCount(Integer routeNumOfTryCount) {
        this.routeNumOfTryCount = routeNumOfTryCount;
    }

    public Integer getRouteNumOfTryUser() {
        return routeNumOfTryUser;
    }

    public void setRouteNumOfTryUser(Integer routeNumOfTryUser) {
        this.routeNumOfTryUser = routeNumOfTryUser;
    }

    public String getRouteStartPointAddress() {
        return routeStartPointAddress;
    }

    public void setRouteStartPointAddress(String routeStartPointAddress) {
        this.routeStartPointAddress = routeStartPointAddress;
    }

    public String getRouteEndPointAddress() {
        return routeEndPointAddress;
    }

    public void setRouteEndPointAddress(String routeEndPointAddress) {
        this.routeEndPointAddress = routeEndPointAddress;
    }

    public double getRouteAvgDegree() {
        return routeAvgDegree;
    }

    public void setRouteAvgDegree(double routeAvgDegree) {
        this.routeAvgDegree = routeAvgDegree;
    }

    public double getRouteMaxAltitude() {
        return routeMaxAltitude;
    }

    public void setRouteMaxAltitude(double routeMaxAltitude) {
        this.routeMaxAltitude = routeMaxAltitude;
    }

    public double getRouteMinAltitude() {
        return routeMinAltitude;
    }

    public void setRouteMinAltitude(double routeMinAltitude) {
        this.routeMinAltitude = routeMinAltitude;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
