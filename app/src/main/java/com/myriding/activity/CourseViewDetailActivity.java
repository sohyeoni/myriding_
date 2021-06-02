package com.myriding.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.myriding.R;
import com.myriding.atapter.MyCourseAdapter;
import com.myriding.http.RetrofitAPI;
import com.myriding.http.RetrofitClient;
import com.myriding.model.CourseData;
import com.myriding.model.CourseResponse;
import com.myriding.model.Token;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseViewDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    final static String TAG = "CourseViewDetailActivity";

    GoogleMap mGoogleMap;

    Button btn_ridingStart;
    ToggleButton btn_like;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_view_detail);

        // SupportMapFragment를 통해 레이아웃에 만든 fragment의 ID를 참조하고 구글맵 호출
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_course_detail);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        int courseID = intent.getExtras().getInt("post_id");

        // todo 해당 경로의 데이터 획득
        getDetailCourse(courseID);

        btn_ridingStart = (Button) findViewById(R.id.start_button_course_detail);
        btn_ridingStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), RidingMain2.class);
//                startActivity(intent);
            }
        });

        btn_like = (ToggleButton) findViewById(R.id.like_button_course_detail);
        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_like.isChecked()) {
                    Log.d(TAG + "[Toggle Button] : ", "enabled");
                } else {
                    Log.d(TAG + "[Toggle Button] : ", "disabled");
                }
            }
        });
    }

    // 구글맵 띄울 준비가 되면 자동 호출
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        LatLng seoul = new LatLng(37.52487, 126.92723);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 16));
    }

    private RetrofitAPI retrofitAPI;
    private void getDetailCourse(int id) {
        retrofitAPI = RetrofitClient.getApiService();

        Call<CourseResponse> call = retrofitAPI.getDetailCourse(Token.getToken(), id);
        call.enqueue(new Callback<CourseResponse>() {
            @Override
            public void onResponse(Call<CourseResponse> call, Response<CourseResponse> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "경로 상세 정보 조회 성공", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, response.body() + "");
                } else {
                    Toast.makeText(getApplicationContext(), "경로 상세 정보 조회 실패1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CourseResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "경로 상세 정보 조회 실패2", Toast.LENGTH_SHORT).show();
                Log.d(TAG, t.getMessage());
            }
        });
    }
}
