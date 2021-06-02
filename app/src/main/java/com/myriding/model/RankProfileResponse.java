package com.myriding.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RankProfileResponse {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("stat")
    @Expose
    private RankData stat;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RankData getRanks() {
        return stat;
    }

    public void setRanks(RankData stat) {
        this.stat = stat;
    }
}
