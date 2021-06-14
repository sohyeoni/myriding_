package com.myriding.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HomeDetailResponse {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("recordValue")
    @Expose
    private RecordValue recordValue;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RecordValue getRecordValue() {
        return recordValue;
    }

    public void setRecordValue(RecordValue recordValue) {
        this.recordValue = recordValue;
    }
}
