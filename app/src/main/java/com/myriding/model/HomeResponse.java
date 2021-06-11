package com.myriding.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HomeResponse {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("homeValue")
    @Expose
    private HomeValue homeValue;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HomeValue getHomeValue() {
        return homeValue;
    }

    public void setHomeValue(HomeValue homeValue) {
        this.homeValue = homeValue;
    }
}
