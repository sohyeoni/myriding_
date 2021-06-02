package com.myriding.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.myriding.R;
import com.myriding.http.RetrofitAPI;
import com.myriding.http.RetrofitClient;
import com.myriding.model.ErrorResponse;
import com.myriding.model.Register;
import com.myriding.model.RegisterResponse;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    private Button btn_signUp;
    private EditText et_id, et_password, et_passwordConfirmation, et_nickname;
    private TextView tv_id, tv_password, tv_passwordConfirmation, tv_nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        et_id = (EditText) findViewById(R.id.signup_id);
        et_password = (EditText) findViewById(R.id.signup_password);
        et_passwordConfirmation = (EditText) findViewById(R.id.signup_pwd_confirmation);
        et_nickname = (EditText) findViewById(R.id.signup_nickname);

        tv_id = (TextView) findViewById(R.id.signup_id_msg);
        tv_password = (TextView) findViewById(R.id.signup_pwd_msg);
        tv_passwordConfirmation = (TextView) findViewById(R.id.signup_pwd_confirmation_msg);
        tv_nickname = (TextView) findViewById(R.id.signup_nickname_msg);

        btn_signUp = (Button) findViewById(R.id.signup_btn);
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = et_id.getText().toString();
                String password = et_password.getText().toString();
                String passwordConfirmation = et_passwordConfirmation.getText().toString();
                String nickname = et_nickname.getText().toString();

                if(vaildateRegisterInfo(account, password, passwordConfirmation, nickname)) {
                    inserRegister(account, password, passwordConfirmation, nickname);
                }
            }
        });
    }

    private RetrofitAPI retrofitAPI;
    private void inserRegister(String account, String password, String passwordConfirmation, String nickname) {
        Register register = new Register(account, password, passwordConfirmation, nickname);

        retrofitAPI = RetrofitClient.getApiService();

        Call<RegisterResponse> registerCall = retrofitAPI.insertRegister(register);
        registerCall.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                RegisterResponse registerResponse = response.body();
                if(registerResponse != null) {
                    if(response.isSuccessful()) {
                        // if(response.code() == 201) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(SignupActivity.this);
                        dialog.setMessage(registerResponse.getMessage());

                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);

                                dialogInterface.dismiss();
                            }
                        });

                        dialog.show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        ErrorResponse errorResponse = new Gson().fromJson(errorBody, ErrorResponse.class);

                        Log.d("HTTP", "error password : " + errorResponse.getData().getError().getUserPassword());
                        Log.d("HTTP", "error nickname : " + errorResponse.getData().getError().getUserNickname());
                        Log.d("HTTP", "error account : " + errorResponse.getData().getError().getUserAccount());

                        if(errorResponse.getData().getError().getUserPassword() != null) {
                            tv_password.setText(errorResponse.getData().getError().getUserPassword().toString());
                            tv_password.setVisibility(View.VISIBLE);
                        } else {
                            tv_password.setText("");
                            tv_password.setVisibility(View.INVISIBLE);
                        }

                        if(errorResponse.getData().getError().getUserNickname() != null) {
                            tv_nickname.setText(errorResponse.getData().getError().getUserNickname().toString());
                            tv_nickname.setVisibility(View.VISIBLE);
                        } else {
                            tv_nickname.setText("");
                            tv_nickname.setVisibility(View.INVISIBLE);
                        }

                        if(errorResponse.getData().getError().getUserAccount() != null) {
                            tv_id.setText(errorResponse.getData().getError().getUserAccount().toString());
                            tv_id.setVisibility(View.VISIBLE);
                        } else {
                            tv_id.setText("");
                            tv_id.setVisibility(View.INVISIBLE);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Log.d("HTTP", "Fail : " + t.getMessage());
            }
        });
    }

    public static boolean isValidPassword(String password) {
        // String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$";
        String regex = "^(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*]).{7,}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    public boolean isValidAccount(String account) {
        // String regex = "^[a-z]{1}[a-z0-9_]{5,20}$";
        String regex = "^[a-z0-9_]{5,15}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(account);

        return matcher.matches();
    }

    public boolean isValidNickname(String nickname) {
        // String regex = "^[a-zA-Z0-9]{8,20}$";
        String regex = "^[ㄱ-ㅎㅏ-ㅣ가-힣0-9]{3,15}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nickname);

        return matcher.matches();
    }

    public boolean vaildateRegisterInfo(String account, String password, String passwordConfirmation, String nickname) {
        boolean isValid = true;

        if(account.getBytes().length <= 0){
            tv_id.setText("필수 정보입니다.");
            tv_id.setVisibility(View.VISIBLE);

            isValid = false;
        } else if(!isValidAccount(account)) {
            tv_id.setText("5~15자의 영문 소문자, 숫자와 특수기호 _만 사용 가능합니다.");
            tv_id.setVisibility(View.VISIBLE);

            isValid = false;
        } else {
            tv_id.setVisibility(View.INVISIBLE);
        }

        if(password.getBytes().length <= 0) {
            tv_password.setText("필수 정보입니다.");
            tv_password.setVisibility(View.VISIBLE);

            isValid = false;
        } else if(!isValidPassword(password)) {
            tv_password.setText("7자 이상 영문 소문자, 숫자, 특수문자를 사용하세요.");
            tv_password.setVisibility(View.VISIBLE);

            isValid = false;
        } else {
            tv_password.setVisibility(View.INVISIBLE);

            if (!password.equals(passwordConfirmation)) {
                tv_passwordConfirmation.setText("비밀번호가 일치하지 않습니다.");
                tv_passwordConfirmation.setVisibility(View.VISIBLE);

                isValid = false;
            } else {
                tv_passwordConfirmation.setVisibility(View.INVISIBLE);
            }
        }

        if(passwordConfirmation.getBytes().length <= 0) {
            tv_passwordConfirmation.setText("필수 정보입니다.");
            tv_passwordConfirmation.setVisibility(View.VISIBLE);

            isValid = false;
        }

        if(nickname.trim().getBytes().length <= 0) {
            tv_nickname.setText("필수 정보입니다.");
            tv_nickname.setVisibility(View.VISIBLE);

            isValid = false;
        } else if(!isValidNickname(nickname)) {
            tv_nickname.setText("5~15자 이내 한글로 입력하세요.");
            tv_nickname.setVisibility(View.VISIBLE);

            isValid = false;
        } else {
            tv_nickname.setVisibility(View.INVISIBLE);
        }

        return isValid;
    }
}