package com.myriding.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myriding.R;
import com.myriding.activity.MyCourseMoreActivity;
import com.myriding.activity.SearchActivity;
import com.myriding.atapter.MyCourseAdapter;
import com.myriding.atapter.PopularCourseAdapter;
import com.myriding.http.RetrofitAPI;
import com.myriding.http.RetrofitClient;
import com.myriding.model.CourseData;
import com.myriding.model.CourseResponse;
import com.myriding.model.MyCourse;
import com.myriding.model.PopularCourse;
import com.myriding.model.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragCourse extends Fragment {
    final static String TAG = "FragCourse";

    private View view;

    private TextView btn_more;
    private TextView btn_search;

    private RecyclerView popualrRecyclerView;
    private PopularCourseAdapter popularCourseAdapter;
    private List<PopularCourse> lstPopularCourse = new ArrayList<>();

    private RecyclerView myCourseRecyclerView;
    private MyCourseAdapter myCourseAdapter;
    private List<MyCourse> lstMyCourse;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_course, container, false);

        /* Search Bar 클릭 이벤트 (SearchActivity로 화면 전환) */
        btn_search = (TextView) view.findViewById(R.id.course_search_btn);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        /* 내 라이딩 코스 more 버튼 Click 이벤트 */
        btn_more = (TextView) view.findViewById(R.id.myCourse_more_btn);
        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyCourseMoreActivity.class);
                startActivity(intent);
            }
        });

        /* 인기 라이딩 코스 리사이클러뷰 초기화 */
        popualrRecyclerView = (RecyclerView) view.findViewById(R.id.popularCourse_recyclerView);
        LinearLayoutManager horizontalManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        popualrRecyclerView.setLayoutManager(horizontalManager);

        /* 내 라이딩 코스 리사이클러뷰 초기화 */
        myCourseRecyclerView = (RecyclerView) view.findViewById(R.id.myCourse_recyclerView);
        LinearLayoutManager verticalManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        myCourseRecyclerView.setLayoutManager(verticalManager);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 인기 라이딩 코스 리스트 생성 (TOP 5)
        lstPopularCourse = new ArrayList<>();
        getPopularCourse();

        // 내 라이딩 코스 리스트 생성 (최신 5개)
        lstMyCourse = new ArrayList<>();
        getMyCourse();
    }

    /* 인기 라이딩 코스 획득 메서드 */
    private RetrofitAPI retrofitAPI;
    private void getPopularCourse() {
        retrofitAPI = RetrofitClient.getApiService();

        Call<CourseResponse> call = retrofitAPI.getPopularCourse(Token.getToken());
        call.enqueue(new Callback<CourseResponse>() {
            @Override
            public void onResponse(Call<CourseResponse> call, Response<CourseResponse> response) {
                if(response.isSuccessful()) {
                    List<CourseData> popularRoute = response.body().getRoutes();
                    setPopularCourseList(popularRoute);

                    popularCourseAdapter = new PopularCourseAdapter(getContext(), lstPopularCourse);
                    popualrRecyclerView.setAdapter(popularCourseAdapter);
                } else {
                    try {
                        String errorBody = response.errorBody().string();

                        Toast.makeText(getContext(), "인기 코스 조회 실패", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CourseResponse> call, Throwable t) {
                Toast.makeText(getContext(), "통신 실패", Toast.LENGTH_SHORT).show();
                Log.d(TAG, t.getMessage());
            }
        });
    }

    /* 내 라이딩 코스 획득 메서드 */
    private void getMyCourse() {
        retrofitAPI = RetrofitClient.getApiService();

        Call<CourseResponse> call = retrofitAPI.getMyCourse(Token.getToken());
        call.enqueue(new Callback<CourseResponse>() {
            @Override
            public void onResponse(Call<CourseResponse> call, Response<CourseResponse> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getContext(), "내 라이딩 경로 조회 성공", Toast.LENGTH_SHORT).show();

                    List<CourseData> myRoute = response.body().getRoutes();
                    setMyCourseList(myRoute);

                    myCourseAdapter = new MyCourseAdapter(getContext(), lstMyCourse);
                    myCourseRecyclerView.setAdapter(myCourseAdapter);
                } else {
                    Toast.makeText(getContext(), "내 라이딩 경로 조회 실패", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, response.body() + "");
                }
            }

            @Override
            public void onFailure(Call<CourseResponse> call, Throwable t) {
                Toast.makeText(getContext(), "서버 통신 실패", Toast.LENGTH_SHORT).show();
                Log.d(TAG, t.getMessage());
            }
        });
    }

    /* 리사이클러뷰에 표시를 위해 획득한 인기 라이딩 정보를 리스트에 저장 */
    private void setPopularCourseList(List<CourseData> routes) {
        for(CourseData route : routes) {
            lstPopularCourse.add(
                    new PopularCourse(
                            route.getId(),
                            route.getRouteTitle(),
                            route.getRouteDistance(),
                            route.getRouteLike(),
                            route.getRouteImage()
                    )
            );
        }
    }

    /* 리사이클러뷰에 표시를 위해 획득한 내 라이딩 코스 정보를 리스트에 저장 */
    private void setMyCourseList(List<CourseData> routes) {
        for(CourseData route : routes) {
            lstMyCourse.add(
                new MyCourse(
                    route.getRouteImage(),
                    route.getId(),
                    route.getRouteUserId(),
                    route.getRouteTitle(),
                    route.getRouteStartPointAddress(),
                    route.getRouteEndPointAddress(),
                    route.getCreatedAt(),
                    route.getRouteDistance(),
                    route.getRouteTime(),
                    route.getRouteLike()
                )
            );
        }
    }
}
