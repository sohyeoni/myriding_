/* Response 데이터
*  인기 라이딩 코스 : id, route_title, route_distance, route_like
*  라이딩 코스 검색 : id, route_title, route_distance, route_like, route_time, route_image, route_user_id,
*                   route_num_of_try_count, route_num_of_try_user,
*                   route_start_point_address, route_end_point_address,
*                   route_avg_degree, route_max_altitude, route_min_altitude,
* 내 라이딩 코스 : id, route_user_id, route_title, route_like, route_distance, route_iamge, route_time,
*                 route_start_point_address, route_end_point_address, created_at
* */
package com.myriding.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class CourseData {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("route_title")
    @Expose
    private String routeTitle;
    @SerializedName("route_distance")
    @Expose
    private double routeDistance;
    @SerializedName("route_like")
    @Expose
    private int routeLike;
    @SerializedName("route_user_id")
    @Expose
    private Integer routeUserId;
    @SerializedName("route_image")
    @Expose
    private String routeImage;
    @SerializedName("route_time")
    @Expose
    private Integer routeTime;
    @SerializedName("route_num_of_try_count")
    @Expose
    private Integer routeNumOfTryCount;
    @SerializedName("route_num_of_try_user")
    @Expose
    private Integer routeNumOfTryUser;
    @SerializedName("route_start_point_address")
    @Expose
    private String routeStartPointAddress;
    @SerializedName("created_at")
    @Expose
    private Date createdAt;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRouteTitle() {
        return routeTitle;
    }

    public void setRouteTitle(String routeTitle) {
        this.routeTitle = routeTitle;
    }

    public double getRouteDistance() {
        return routeDistance;
    }

    public void setRouteDistance(double routeDistance) {
        this.routeDistance = routeDistance;
    }

    public int getRouteLike() {
        return routeLike;
    }

    public void setRouteLike(int routeLike) {
        this.routeLike = routeLike;
    }

    public Integer getRouteUserId() {
        return routeUserId;
    }

    public void setRouteUserId(Integer routeUserId) {
        this.routeUserId = routeUserId;
    }

    public String getRouteImage() {
        return routeImage;
    }

    public void setRouteImage(String routeImage) {
        this.routeImage = routeImage;
    }

    public Integer getRouteTime() {
        return routeTime;
    }

    public void setRouteTime(Integer routeTime) {
        this.routeTime = routeTime;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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
}
