package com.myriding.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RankResponse {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("ranks")
    @Expose
    private List<RankData> ranks;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<RankData> getRanks() {
        return ranks;
    }

    public void setRanks(List<RankData> ranks) {
        this.ranks = ranks;
    }
}
