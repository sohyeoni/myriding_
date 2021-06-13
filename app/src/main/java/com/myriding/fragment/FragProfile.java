package com.myriding.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myriding.R;
import com.myriding.activity.SearchActivity;
import com.myriding.atapter.ProfileRecyclerViewAdapter;
import com.myriding.atapter.SearchRecyclerViewAdapter;
import com.myriding.http.RetrofitAPI;
import com.myriding.http.RetrofitClient;
import com.myriding.model.CourseData;
import com.myriding.model.CourseResponse;
import com.myriding.model.PopularCourse;
import com.myriding.model.Profile;
import com.myriding.model.ProfileResponse;
import com.myriding.model.Stat;
import com.myriding.model.Token;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragProfile extends Fragment {
    private String TAG = "FragProfile";
    private String DISTANCE_CHART_COLOR = "#FFB85A";
    private String TIME_CHART_COLOR = "#9FC93C";
    private String AVG_SPEED_CHART_COLOR = "#1266FF";
    private String MAX_SPEED_CHART_COLOR = "#C98AFF";
    private int UNCLICKED_BUTTON_COLOR = Color.parseColor("#d3d3d3");

    private View view;

    private RadioButton rb_Left, rb_Right, rb_Center;
    private RadioGroup radioGroup;

    private ImageView img_picture;
    private TextView tv_username, tv_score, tv_count;

    private LineChart chart;
    List<Entry> distances = new ArrayList<>();
    List<Entry> avgSpeeds = new ArrayList<>();
    List<Entry> maxSpeeds = new ArrayList<>();
    List<Entry> times = new ArrayList<>();

    private TextView tv_badgeMore;
    private RecyclerView myrecyclerview;
    private List<Profile> lstBadge;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_profile, container, false);

        rb_Left = (RadioButton) view.findViewById(R.id.rbLeft);
        rb_Right = (RadioButton) view.findViewById(R.id.rbRight);
        rb_Center = (RadioButton) view.findViewById(R.id.rbCenter);

        tv_username = (TextView) view.findViewById(R.id.profile_name);
        tv_score = (TextView) view.findViewById(R.id.profile_score);
        tv_count = (TextView) view.findViewById(R.id.profile_count);

        img_picture = (ImageView) view.findViewById(R.id.profile_img);
        img_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 사진을 갤러리에서 가져오거나 찍어서 가져오기
            }
        });

        radioGroup = (RadioGroup) view.findViewById(R.id.rbGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbLeft:
                        changeRadioButtonTextColor(Color.WHITE, UNCLICKED_BUTTON_COLOR, UNCLICKED_BUTTON_COLOR);
                        makeSingleChart(distances, "거리", DISTANCE_CHART_COLOR);
                        break;
                    case R.id.rbCenter:
                        changeRadioButtonTextColor(UNCLICKED_BUTTON_COLOR, Color.WHITE, UNCLICKED_BUTTON_COLOR);
                        makeDoubleChart(avgSpeeds, "평균속도", AVG_SPEED_CHART_COLOR, maxSpeeds, "최고 속도", MAX_SPEED_CHART_COLOR);
                        break;
                    case R.id.rbRight:
                        changeRadioButtonTextColor(UNCLICKED_BUTTON_COLOR, UNCLICKED_BUTTON_COLOR, Color.WHITE);
                        makeSingleChart(times, "시간", TIME_CHART_COLOR);
                        break;
                }
            }
        });

        getProfile();

        // <-- 통계 차트 초기화
        chart = (LineChart) view.findViewById(R.id.chart);
        List<Entry> defaultData = new ArrayList<>();

        int currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        for (int i = 0; i < 12; i++) {
            currentWeek = currentWeek - 1;
            defaultData.add(new Entry(currentWeek, 0f));
        }
        Collections.sort(defaultData, new EntryXComparator());
        makeSingleChart(defaultData, "거리", DISTANCE_CHART_COLOR);
        // -->

        // <-- 배찌 "more" 클릭 이벤트
        tv_badgeMore = (TextView) view.findViewById(R.id.badge_more_btn);
        tv_badgeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), BadgeHomeActivity.class);
//                startActivity(intent);
            }
        });
        // -->

        // <-- 배찌 목록 생성
//        myrecyclerview = (RecyclerView) view.findViewById(R.id.profile_recyclerview);
//        ProfileRecyclerViewAdapter recyclerAdapter = new ProfileRecyclerViewAdapter(getContext(), lstBadge);
//        // RecyclerView item 배치 Horizontal로 설정
//        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//        myrecyclerview.setAdapter(recyclerAdapter);
        // -->

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // <-- 배찌 리스트 생성
//        lstBadge = new ArrayList<>();
//
//        lstBadge.add(new Profile(R.drawable.ic_badge, "DISTANCE", "2 / 4 개"));
//        lstBadge.add(new Profile(R.drawable.ic_badge, "SPEED", "1 / 4 개"));
//        lstBadge.add(new Profile(R.drawable.ic_badge, "TIME", "1 / 4 개"));
//        lstBadge.add(new Profile(R.drawable.ic_badge, "PERIOD", "4 / 7 개"));
//        lstBadge.add(new Profile(R.drawable.ic_badge, "SCORE", "5 / 6 개"));
        // -->
    }

    // <-- 라디오 버튼 클릭 시 텍스트 색상 변경 메서드
    private void changeRadioButtonTextColor(int left, int center, int right) {
        rb_Left.setTextColor(left);
        rb_Center.setTextColor(center);
        rb_Right.setTextColor(right);
    }
    // -->


    /* 사용자 정보 / 통계 / 배찌 획득 메서드 */
    private RetrofitAPI retrofitAPI;
    private void getProfile() {
        retrofitAPI = RetrofitClient.getApiService();

        Call<ProfileResponse> call = retrofitAPI.getProfile(Token.getToken());
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if(response.isSuccessful()) {
                    Profile profile = response.body().getProfile();

                    if(profile != null) {
                        setUserData(profile);
                        setGraphDatas(profile.getStat());
                        makeSingleChart(distances, "거리", DISTANCE_CHART_COLOR);
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.d(TAG, "라이딩 코스 검색 실패1");
                        Log.d(TAG, errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                t.getMessage();
                Log.d(TAG, "라이딩 코스 검색 실패2");
                Log.d(TAG, t.getMessage());
            }
        });
    }

    void setUserData(Profile profile) {
        tv_username.setText(profile.getUserNickname());
        tv_score.setText(profile.getUserScoreOfRiding() + "점");
        tv_count.setText(profile.getUserNumOfRiding() + "회");

        try {
            String imgString = profile.getUserPicture().substring(22);
            byte[] imageBytes = Base64.decode(imgString, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            img_picture.setImageBitmap(decodedImage);
        } catch (Exception e) {
            img_picture.setImageResource(R.drawable.img_user);
        }
    }

    private void setGraphDatas(List<Stat> stats) {
        for (Stat stat : stats) {
            distances.add(new Entry(stat.getWeek(), (float) stat.getDistance()));
            avgSpeeds.add(new Entry(stat.getWeek(), (float) stat.getAvgSpeed()));
            maxSpeeds.add(new Entry(stat.getWeek(), (float) stat.getMaxSpeed()));
            times.add(new Entry(stat.getWeek(), (int) (stat.getTime() / 60)));
        }

        Collections.sort(distances, new EntryXComparator());
        Collections.sort(avgSpeeds, new EntryXComparator());
        Collections.sort(maxSpeeds, new EntryXComparator());
        Collections.sort(times, new EntryXComparator());
    }

    private void initializeChart() {
        Description description = new Description();
        description.setText("");

        chart.setDoubleTapToZoomEnabled(false);
        chart.setTouchEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDescription(description);
        chart.animateY(800);

        // x, y축 값과 그래프 축 사이 간격
        chart.getXAxis().setYOffset(10f);
        chart.getAxisLeft().setXOffset(10f);

        chart.invalidate();

        // 차트 X, Y축 설정
        // X축 설정 (레이블 위치, 텍스트 색상, 레이블 표시 개수)
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(11);
        xAxis.setSpaceMax(0.5f);
        xAxis.setSpaceMin(0.5f);

        // Y축 왼쪽 레이블 설정
        YAxis yLAxis = chart.getAxisLeft();
        yLAxis.setTextColor(Color.GRAY);
        yLAxis.setAxisMinimum(0);
        yLAxis.enableGridDashedLine(10, 10, 10);
        yLAxis.setDrawZeroLine(true);
        yLAxis.setZeroLineColor(Color.BLACK);

        // Y축 오른쪽 레이블 설정 (비활성화)
        YAxis yRAxis = chart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);
    }

    private LineDataSet initializeLineDataSet(List<Entry> entries, String label, String chartColor) {
        initializeChart();

        LineDataSet lineDataSet = new LineDataSet(entries, label);

        lineDataSet.setLineWidth(2);
        lineDataSet.setColor(Color.parseColor(chartColor));

        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleHoleRadius(4);
        lineDataSet.setCircleColor(Color.parseColor(chartColor));
        lineDataSet.setCircleHoleColor(Color.WHITE);

        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawValues(false);

        return lineDataSet;
    }

    private void makeSingleChart(List<Entry> entries, String label, String chartColor) {
        LineDataSet lineDataSet = initializeLineDataSet(entries, label, chartColor);

        LineData data = new LineData(lineDataSet);
        chart.setData(data);
    }


    private void makeDoubleChart(List<Entry> entries1, String label1, String chartColor1, List<Entry> entries2, String label2, String chartColor2) {
        LineDataSet lineDataSet1 = initializeLineDataSet(entries1, label1, chartColor1);
        LineDataSet lineDataSet2 = initializeLineDataSet(entries2, label2, chartColor2);

        LineData data = new LineData(lineDataSet1, lineDataSet2);
        chart.setData(data);
    }
}
