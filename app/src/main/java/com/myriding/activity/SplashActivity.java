package com.myriding.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.myriding.PreferenceManager;
import com.myriding.R;
import com.myriding.activity.BottomNavigationActivity;
import com.myriding.activity.LoginActivity;
import com.myriding.http.RetrofitAPI;
import com.myriding.http.RetrofitClient;
import com.myriding.model.ErrorResponse;
import com.myriding.model.Login;
import com.myriding.model.LoginResponse;
import com.myriding.model.Token;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    Intent loginIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String user_id = PreferenceManager.getString(getApplicationContext(), "user_id");
        String user_pwd = PreferenceManager.getString(getApplicationContext(), "user_pwd");

        loginIntent = new Intent(getApplicationContext(), LoginActivity.class);

        if(user_id != "" && user_pwd != "") {
            requestLogin(user_id, user_pwd);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(loginIntent);
                    finish();
                }
            },3000);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private RetrofitAPI retrofitAPI;
    private void requestLogin(String user_account, String user_password) {
        Login login = new Login(user_account, user_password);

        retrofitAPI = RetrofitClient.getApiService();

        Call<LoginResponse> loginResponseCall = retrofitAPI.login(login);
        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()) {
                    String token = response.body().getData().getToken();
                    Token.setToken("Bearer " + token);

                    PreferenceManager.setString(getApplicationContext(), "user_token", "Bearer " + token);

                    Intent intent = new Intent(getApplicationContext(), BottomNavigationActivity.class);
                    startActivity(intent);
                } else {
                    startActivity(loginIntent);
                }
                finish();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d("SplashActivity", t.getMessage());
                startActivity(loginIntent);
                finish();
            }
        });
    }
}