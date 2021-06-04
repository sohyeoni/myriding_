package com.myriding.model;

public class Register {
    private String user_account;
    private String user_password;
    private String user_password_confirmation;
    private String user_nickname;
    private String user_picture;

    public Register(String user_account, String user_password, String user_password_confirmation, String user_nickname) {
        this.user_account = user_account;
        this.user_password = user_password;
        this.user_password_confirmation = user_password_confirmation;
        this.user_nickname = user_nickname;
    }

    public Register(String user_account, String user_password, String user_password_confirmation, String user_nickname, String user_picture) {
        this.user_account = user_account;
        this.user_password = user_password;
        this.user_password_confirmation = user_password_confirmation;
        this.user_nickname = user_nickname;
        this.user_picture = user_picture;
    }

    public String getUser_account() {
        return user_account;
    }

    public void setUser_account(String user_account) {
        this.user_account = user_account;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_password_confirmation() {
        return user_password_confirmation;
    }

    public void setUser_password_confirmation(String user_password_confirmation) {
        this.user_password_confirmation = user_password_confirmation;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getUser_picture() {
        return user_picture;
    }

    public void setUser_picture(String user_picture) {
        this.user_picture = user_picture;
    }
}
