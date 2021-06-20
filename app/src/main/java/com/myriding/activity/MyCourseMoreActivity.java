package com.myriding.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.myriding.R;
import com.myriding.atapter.MyCourseAdapter;
import com.myriding.http.RetrofitAPI;
import com.myriding.http.RetrofitClient;
import com.myriding.model.CourseData;
import com.myriding.model.CourseResponse;
import com.myriding.model.MyCourse;
import com.myriding.model.Token;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCourseMoreActivity extends AppCompatActivity {
    final static String TAG = "MyCourseMoreActivity";

    private RecyclerView myRecyclerview;
    private MyCourseAdapter recyclerAdapter;
    private List<MyCourse> lstMyCourse = new ArrayList<>();

    private ImageView img_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_course_more);

        img_loading = (ImageView) findViewById(R.id.my_course_more_loading);
        Glide.with(this).load(R.raw.gif_loading).into(img_loading);

        myRecyclerview = (RecyclerView) findViewById(R.id.my_course_more_recyclerview);
        myRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        getMyCourseAll();
    }

    /* 내 라이딩 코스 전체 획득 메서드 */
    private RetrofitAPI retrofitAPI;
    private void getMyCourseAll() {
        retrofitAPI = RetrofitClient.getApiService();

        Call<CourseResponse> call = retrofitAPI.getMyCourseAll(Token.getToken());
        call.enqueue(new Callback<CourseResponse>() {
            @Override
            public void onResponse(Call<CourseResponse> call, Response<CourseResponse> response) {
                if(response.isSuccessful()) {
                    // Toast.makeText(getApplicationContext(), "내 라이딩 경로 전체 조회 성공", Toast.LENGTH_SHORT).show();

                    List<CourseData> myRoute = response.body().getRoutes();
                    setMyCourseList(myRoute);

                    recyclerAdapter = new MyCourseAdapter(getApplicationContext(), lstMyCourse);
                    myRecyclerview.setAdapter(recyclerAdapter);
                } else {
                    Toast.makeText(getApplicationContext(), "내 라이딩 경로 전체 조회 실패", Toast.LENGTH_SHORT).show();
                }

                img_loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<CourseResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "내 라이딩 경로 전체 조회 실패", Toast.LENGTH_SHORT).show();
                Log.d(TAG, t.getMessage());

                img_loading.setVisibility(View.GONE);
            }
        });
    }

    /* 리사이클러뷰에 표시를 위해 획득한 내 라이딩 코스 정보를 리스트에 저장 */
    private void setMyCourseList(List<CourseData> routes) {
        for(CourseData route : routes) {
            lstMyCourse.add(new MyCourse(route.getRouteImage(), route.getId(), route.getRouteUserId(), route.getRouteTitle(),
                    route.getRouteStartPointAddress(), route.getRouteEndPointAddress(), route.getCreatedAt(), route.getRouteDistance(),
                    route.getRouteTime(), route.getRouteLike()));
        }
    }
}