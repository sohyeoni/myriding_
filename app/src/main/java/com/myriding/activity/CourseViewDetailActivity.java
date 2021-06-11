package com.myriding.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.myriding.R;
import com.myriding.atapter.MyCourseAdapter;
import com.myriding.http.RetrofitAPI;
import com.myriding.http.RetrofitClient;
import com.myriding.model.CourseData;
import com.myriding.model.CourseDetailResponse;
import com.myriding.model.CourseResponse;
import com.myriding.model.RouteLikeResponse;
import com.myriding.model.RouteMongoValue;
import com.myriding.model.RouteValue;
import com.myriding.model.Routes;
import com.myriding.model.Token;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseViewDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    final static String TAG = "CourseViewDetailActivity";

    GoogleMap mGoogleMap;

    Button btn_ridingStart;
    ToggleButton btn_like;

    TextView tv_date, tv_courseName, tv_startPoint, tv_endPoint, tv_distance, tv_time, tv_like;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_view_detail);

        tv_date = (TextView) findViewById(R.id.created_date_course_detail);
        tv_courseName = (TextView) findViewById(R.id.course_name_course_detail);
        tv_startPoint = (TextView) findViewById(R.id.start_point_course_detail);
        tv_endPoint = (TextView) findViewById(R.id.end_point_course_detail);
        tv_distance = (TextView) findViewById(R.id.distance_course_detail);
        tv_time = (TextView) findViewById(R.id.prediction_time_course_detail);
        tv_like = (TextView) findViewById(R.id.like_number_course_detail);

        // SupportMapFragment를 통해 레이아웃에 만든 fragment의 ID를 참조하고 구글맵 호출
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_course_detail);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        int courseID = intent.getExtras().getInt("post_id");

        getDetailCourse(courseID);

        btn_ridingStart = (Button) findViewById(R.id.start_button_course_detail);
        btn_ridingStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RidingActivity.class);
                startActivity(intent);
            }
        });

        btn_like = (ToggleButton) findViewById(R.id.like_button_course_detail);
        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_like.isChecked()) {
                    updateLike(courseID);
                } else {
                    deleteLike(courseID);
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

        Call<CourseDetailResponse> call = retrofitAPI.getDetailCourse(Token.getToken(), id);
        call.enqueue(new Callback<CourseDetailResponse>() {
            @Override
            public void onResponse(Call<CourseDetailResponse> call, Response<CourseDetailResponse> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "경로 상세 정보 조회 성공", Toast.LENGTH_SHORT).show();
                    List<RouteValue> courseDetail = response.body().getRoutes().getRouteValue();
                    List<RouteMongoValue> routeValues = response.body().getRoutes().getRouteMongoValue();

                    if(courseDetail != null)    setCourseInfo(courseDetail);
                    if(routeValues != null)     setRoutes(routeValues);

                    boolean likeStatus =  response.body().getRoutes().getRouteLikeStatus() == 1 ? true : false;
                    btn_like.setChecked(likeStatus);
                } else {
                    Toast.makeText(getApplicationContext(), "경로 상세 정보 조회 실패1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CourseDetailResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "경로 상세 정보 조회 실패2", Toast.LENGTH_SHORT).show();
                Log.d(TAG, t.getMessage());
            }
        });
    }

    void setCourseInfo(List<RouteValue> infos) {
        for(RouteValue info : infos) {
            tv_date.setText(info.getCreatedAt());
            tv_courseName.setText(info.getRouteTitle());
            tv_startPoint.setText(info.getRouteStartPointAddress());
            tv_endPoint.setText(info.getRouteEndPointAddress());
            tv_distance.setText(info.getRouteDistance() + "km");
            tv_time.setText(info.getRouteTime() + "분");
            tv_like.setText(info.getRouteLike() + "");
        }
    }

    PolylineOptions polylineOptions = new PolylineOptions();
    ArrayList<LatLng> arrayPoints = new ArrayList<LatLng>();
    void setRoutes(List<RouteMongoValue> routes) {
        for(RouteMongoValue route : routes) {
            arrayPoints.add(new LatLng(route.getLat(), route.getLng()));
        }

        int center = (int) (routes.size() / 2);
        LatLng latLng = new LatLng(routes.get(center).getLat(), routes.get(center).getLng());

        polylineOptions.color(Color.RED);
        polylineOptions.width(10);
        polylineOptions.addAll(arrayPoints);

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        mGoogleMap.addPolyline(polylineOptions);
    }

    private void updateLike(int id) {
        retrofitAPI = RetrofitClient.getApiService();

        Call<RouteLikeResponse> call = retrofitAPI.updateLike(Token.getToken(), id);
        call.enqueue(new Callback<RouteLikeResponse>() {
            @Override
            public void onResponse(Call<RouteLikeResponse> call, Response<RouteLikeResponse> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "좋아요", Toast.LENGTH_SHORT).show();
                    tv_like.setText(response.body().getLikeCount() + "");
                } else {
                    Toast.makeText(getApplicationContext(), "좋아요 실패1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RouteLikeResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "좋아요 실패2", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteLike(int id) {
        retrofitAPI = RetrofitClient.getApiService();

        Call<RouteLikeResponse> call = retrofitAPI.deleteLike(Token.getToken(), id);
        call.enqueue(new Callback<RouteLikeResponse>() {
            @Override
            public void onResponse(Call<RouteLikeResponse> call, Response<RouteLikeResponse> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "좋아요 취소", Toast.LENGTH_SHORT).show();
                    tv_like.setText(response.body().getLikeCount() + "");
                } else {
                    Toast.makeText(getApplicationContext(), "좋아요 취소 실패1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RouteLikeResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "좋아요 취소 실패2", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
