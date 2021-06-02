package com.myriding.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Error {
    @SerializedName("user_password")
    @Expose
    private List<String> userPassword = null;

    @SerializedName("user_nickname")
    @Expose
    private List<String> userNickname = null;

    @SerializedName("user_account")
    @Expose
    private List<String> userAccount = null;

    public List<String> getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(List<String> userPassword) {
        this.userPassword = userPassword;
    }

    public List<String> getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(List<String> userNickname) {
        this.userNickname = userNickname;
    }

    public List<String> getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(List<String> userAccount) {
        this.userAccount = userAccount;
    }
}
