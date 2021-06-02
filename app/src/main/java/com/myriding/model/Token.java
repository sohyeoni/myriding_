package com.myriding.model;

public class Token {
    private static String myToken;

    public static void setToken(String token) {
        if(myToken == null)
            myToken = token;
    }

    public static String getToken() {
        return myToken;
    }
}
