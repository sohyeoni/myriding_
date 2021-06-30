package com.myriding.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.myriding.R;
import com.myriding.http.RetrofitAPI;
import com.myriding.http.RetrofitClient;
import com.myriding.model.CourseDetailResponse;
import com.myriding.model.RouteLikeResponse;
import com.myriding.model.RouteMongoValue;
import com.myriding.model.RouteValue;
import com.myriding.model.Token;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseViewDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    final static String TAG = "CourseViewDetailActivity";

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat likeFormat = new DecimalFormat("#,###");

    GoogleMap mGoogleMap;

    Button btn_ridingStart;
    ToggleButton btn_like;

    TextView tv_date, tv_courseName, tv_point, tv_distance, tv_time, tv_like;
    ImageView img_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_view_detail);

        tv_date = (TextView) findViewById(R.id.created_date_course_detail);
        tv_courseName = (TextView) findViewById(R.id.course_name_course_detail);
        tv_point = (TextView) findViewById(R.id.point_course_detail);
        tv_distance = (TextView) findViewById(R.id.distance_course_detail);
        tv_time = (TextView) findViewById(R.id.prediction_time_course_detail);
        tv_like = (TextView) findViewById(R.id.like_number_course_detail);

        tv_point.setSingleLine(true);
        tv_point.setEllipsize(TextUtils.TruncateAt.END);

        img_loading = (ImageView) findViewById(R.id.course_detail_loading_image);
        Glide.with(this).load(R.raw.gif_loading).into(img_loading);

        // SupportMapFragment를 통해 레이아웃에 만든 fragment의 ID를 참조하고 구글맵 호출
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_course_detail);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        int courseID = intent.getExtras().getInt("post_id");
        String courseName = intent.getStringExtra("post_name");
        tv_courseName.setText(courseName);

        getDetailCourse(courseID);

        btn_ridingStart = (Button) findViewById(R.id.start_button_course_detail);
        btn_ridingStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RidingActivity.class);
                intent.putExtra("course_id", courseID);
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

        // LatLng seoul = new LatLng(37.52487, 126.92723);
        // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 16));
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
    }

    private RetrofitAPI retrofitAPI;
    private void getDetailCourse(int id) {
        retrofitAPI = RetrofitClient.getApiService();

        Call<CourseDetailResponse> call = retrofitAPI.getDetailCourse(Token.getToken(), id);
        call.enqueue(new Callback<CourseDetailResponse>() {
            @Override
            public void onResponse(Call<CourseDetailResponse> call, Response<CourseDetailResponse> response) {
                if(response.isSuccessful()) {
                    // Toast.makeText(getApplicationContext(), "경로 상세 정보 조회 성공", Toast.LENGTH_SHORT).show();
                    List<RouteValue> courseDetail = response.body().getRoutes().getRouteValue();
                    List<RouteMongoValue> routeValues = response.body().getRoutes().getRouteMongoValue();

                    if(courseDetail != null)    setCourseInfo(courseDetail);
                    if(routeValues != null)     setRoutes(routeValues);

                    boolean likeStatus =  response.body().getRoutes().getRouteLikeStatus() == 1 ? true : false;
                    btn_like.setChecked(likeStatus);
                } else {
                    Toast.makeText(getApplicationContext(), "경로 상세정보 조회 실패", Toast.LENGTH_SHORT).show();
                }

                btn_ridingStart.setVisibility(View.VISIBLE);
                img_loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<CourseDetailResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "경로 상세정보 조회 실패", Toast.LENGTH_SHORT).show();
                Log.d(TAG, t.getMessage());

                img_loading.setVisibility(View.GONE);
            }
        });
    }

    void setCourseInfo(List<RouteValue> infos) {
        for(RouteValue info : infos) {
            tv_date.setText(info.getCreatedAt().substring(0, 10));
            tv_courseName.setText(info.getRouteTitle());
            tv_point.setText(info.getRouteStartPointAddress() + " ~ " + info.getRouteEndPointAddress());
            tv_distance.setText(String.format("%.2f", info.getRouteDistance()) + "km");
            tv_time.setText(info.getRouteTime() + "분");
            tv_like.setText(likeFormat.format(info.getRouteLike()) + "");
        }
    }

    PolylineOptions polylineOptions = new PolylineOptions();
    ArrayList<LatLng> arrayPoints = new ArrayList<LatLng>();
    void setRoutes(List<RouteMongoValue> routes) {
        for(RouteMongoValue route : routes) {
            arrayPoints.add(new LatLng(route.getLat(), route.getLng()));
        }

        polylineOptions.color(Color.RED);
        polylineOptions.width(10);
        polylineOptions.addAll(arrayPoints);

        LatLng startPosition = new LatLng(
                routes.get(0).getLat(), routes.get(0).getLng()
        );

        int size = routes.size();
        LatLng endPosition = new LatLng(
                routes.get(size - 1).getLat(), routes.get(size - 1).getLng()
        );
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(startPosition).include(endPosition);

        if(arrayPoints.size() > 4) {
            int offset = (int) (arrayPoints.size() / 4);

            LatLng secondPosition = new LatLng(routes.get(offset).getLat(), routes.get(offset).getLng());
            LatLng thirdPosition = new LatLng(routes.get(offset*2).getLat(), routes.get(offset*2).getLng());
            builder.include(secondPosition).include(thirdPosition);
        }

        LatLngBounds bounds = builder.build();
        // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 350));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
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
