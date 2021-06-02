package com.myriding.model;

public class Login {
    private String user_account;
    private String user_password;

    public Login(String account, String password) {
        this.user_account = account;
        this.user_password = password;
    }

    public String getAccount() {
        return user_account;
    }

    public void setAccount(String account) {
        this.user_account = account;
    }

    public String getPassword() {
        return user_password;
    }

    public void setPassword(String password) {
        this.user_password = password;
    }
}
