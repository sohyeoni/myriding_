package com.myriding.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HomeValue {
    @SerializedName("TodayValue")
    @Expose
    private List<Integer> todayValue = null;
    @SerializedName("mysqlValue")
    @Expose
    private List<MysqlValue> mysqlValue = null;
    @SerializedName("mongoValue")
    @Expose
    private List<MongoValue> mongoValue = null;

    public List<Integer> getTodayValue() {
        return todayValue;
    }

    public void setTodayValue(List<Integer> todayValue) {
        this.todayValue = todayValue;
    }

    public List<MysqlValue> getMysqlValue() {
        return mysqlValue;
    }

    public void setMysqlValue(List<MysqlValue> mysqlValue) {
        this.mysqlValue = mysqlValue;
    }

    public List<MongoValue> getMongoValue() {
        return mongoValue;
    }

    public void setMongoValue(List<MongoValue> mongoValue) {
        this.mongoValue = mongoValue;
    }
}
