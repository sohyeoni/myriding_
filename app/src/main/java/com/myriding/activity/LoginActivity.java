package com.myriding.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.myriding.PreferenceManager;
import com.myriding.R;
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

public class LoginActivity extends AppCompatActivity {
    private Button      btn_login;
    private TextView    btn_signUp;
    private TextView    tv_errMsg;
    private EditText    et_id, et_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_id = (EditText) findViewById(R.id.login_id);
        et_pwd = (EditText) findViewById(R.id.login_password);
        tv_errMsg = (TextView) findViewById(R.id.login_msg);
        btn_login = (Button) findViewById(R.id.login_btn);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = et_id.getText().toString();
                String userPassword = et_pwd.getText().toString();

                requestLogin(userId, userPassword);
            }
        });

        btn_signUp = (TextView) findViewById(R.id.login_signup_btn);
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        // 이전에 로그인 기록이 있을 경우, 자동 로그인
        if(PreferenceManager.getString(getApplicationContext(), "user_id") != "" &&
                PreferenceManager.getString(getApplicationContext(), "user_pwd") != "") {

            requestLogin(PreferenceManager.getString(getApplicationContext(), "user_id"),
                    PreferenceManager.getString(getApplicationContext(), "user_pwd"));
        }
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
                    tv_errMsg.setText("");
                    tv_errMsg.setVisibility(View.INVISIBLE);

                    String token = response.body().getData().getToken();
                    Token.setToken("Bearer " + token);

                    // Toast.makeText(getApplicationContext(), "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();

                    // 로그인 유지를 위해 사용자 아이디와 비밀번호, 토큰 저장
                    PreferenceManager.setString(getApplicationContext(), "user_id", user_account);
                    PreferenceManager.setString(getApplicationContext(), "user_pwd", user_password);
                    PreferenceManager.setString(getApplicationContext(), "user_token", "Bearer " + token);

                    Intent intent = new Intent(getApplicationContext(), BottomNavigationActivity.class);
                    startActivity(intent);
                } else {
                    try {
                        String errorBody = response.errorBody().string();

                        ErrorResponse errorResponse = new Gson().fromJson(errorBody, ErrorResponse.class);

                        tv_errMsg.setText(errorResponse.getData().getMessage());
                        tv_errMsg.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d("HTTP", "Fail : " + t.getMessage());
            }
        });
    }
}