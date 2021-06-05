package com.myriding.model;

public class Token {
    private static String myToken;

    public static void setToken(String token) {
        myToken = token;
    }

    public static String getToken() {
        return myToken;
    }
}
