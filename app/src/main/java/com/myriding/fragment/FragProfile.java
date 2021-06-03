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

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragProfile extends Fragment {
    private String TAG = "FragProfile";

    private View view;

    private RadioButton rb_Left, rb_Right, rb_Center;
    private RadioGroup radioGroup;

    private ImageView img_picture;
    private TextView tv_username, tv_score, tv_count;

    private LineChart lineChart;

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

        img_picture = (ImageView) view.findViewById(R.id.profile_img);
        tv_username = (TextView) view.findViewById(R.id.profile_name);
        tv_score = (TextView) view.findViewById(R.id.profile_score);
        tv_count = (TextView) view.findViewById(R.id.profile_count);

        radioGroup = (RadioGroup) view.findViewById(R.id.rbGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbLeft:
                        changeRadioButtonTextColor(Color.WHITE, Color.parseColor("#d3d3d3"), Color.parseColor("#d3d3d3"));
                        int[][] myData1 = {{0, 10}, {1, 50}, {2, 6}, {3, 18}, {4, 66},
                                {5, 21}, {6, 28}, {7, 11}, {8, 8}, {9, 35}};
                        setChart(myData1, "#FFB85A", "거리");
                        break;
                    case R.id.rbCenter:
                        changeRadioButtonTextColor(Color.parseColor("#d3d3d3"), Color.WHITE, Color.parseColor("#d3d3d3"));
                        int[][] myData2 = {{0, 15}, {1, 33}, {2, 21}, {3, 12}, {4, 22},
                                {5, 11}, {6, 28}, {7, 33}, {8, 32}, {9, 33}};
                        int[][] myData3 = {{0, 20}, {1, 43}, {2, 24}, {3, 18}, {4, 38},
                                {5, 14}, {6, 32}, {7, 38}, {8, 37}, {9, 48}};
                        setChartMulti(myData2, myData3,"#1266FF", "#C98AFF","평균속도", "최고속도");
                        break;
                    case R.id.rbRight:
                        changeRadioButtonTextColor(Color.parseColor("#d3d3d3"), Color.parseColor("#d3d3d3"), Color.WHITE);
                        int[][] myData4 = {
                                {0, 11}, {1, 48}, {2, 12}, {3, 5}, {4, 35},
                                {5, 9}, {6, 28}, {7, 33}, {8, 32}, {9, 33}, {10, 50}
                        };
                        setChart(times, "#9FC93C", "시간");
                        break;
                }
            }
        });

        getProfile();

        // <-- 통계 차트 초기화
        lineChart = (LineChart) view.findViewById(R.id.chart);
//        int[][] myData = {{0, 10}, {1, 50}, {2, 6}, {3, 18}, {4, 66},
//                {5, 21}, {6, 28}, {7, 11}, {8, 8}, {9, 35}};
//        setChart(myData, "#FFB85A", "거리");
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

    private ArrayList<Entry> lineChartDataSet2(int[][] datas) {
        ArrayList<Entry> dataSet = new ArrayList<>();

        for(int i = 0; i < datas.length; i++) {
            dataSet.add(new Entry(datas[i][0], datas[i][1]));
        }

        return dataSet;
    }

    private void initChart() {
        // 차트 설정
        Description description = new Description();
        description.setText("");

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setTouchEnabled(false);
        lineChart.setDescription(description);
        lineChart.invalidate();

        // 차트 X, Y축 설정
        // X축 설정 (레이블 위치, 텍스트 색상, 레이블 표시 개수)
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setLabelCount(8);

        // Y축 왼쪽 레이블 설정
        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);
        yLAxis.setAxisMinimum(0);

        // Y축 오른쪽 레이블 설정 (비활성화)
        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);
    }

    private void setChart(int[][] data, String color, String label) {
        LineDataSet lineDataSet = new LineDataSet(lineChartDataSet2(data), label);
        lineDataSet.setCircleRadius(5);
        lineDataSet.setColor(Color.parseColor(color));
        lineDataSet.setCircleColor(Color.parseColor(color));
        lineDataSet.setCircleHoleColor(Color.parseColor(color));
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawValues(false);

        // 차트에 데이터 입력
        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);

        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);

        initChart();
    }

    private void setChartMulti(int[][] data, int[][] data2, String color, String color2, String label, String label2) {
        LineDataSet lineDataSet = new LineDataSet(lineChartDataSet2(data), label);

        lineDataSet.setCircleRadius(5);
        lineDataSet.setColor(Color.parseColor(color));
        lineDataSet.setCircleColor(Color.parseColor(color));
        lineDataSet.setCircleHoleColor(Color.parseColor(color));
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawValues(false);

        LineDataSet lineDataSet2 = new LineDataSet(lineChartDataSet2(data2), label2);
        lineDataSet2.setCircleRadius(5);
        lineDataSet2.setColor(Color.parseColor(color2));
        lineDataSet2.setCircleColor(Color.parseColor(color2));
        lineDataSet2.setCircleHoleColor(Color.parseColor(color2));
        lineDataSet2.setDrawCircles(true);
        lineDataSet2.setDrawCircleHole(true);
        lineDataSet2.setDrawValues(false);

        // 차트에 데이터 입력
        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);
        iLineDataSets.add(lineDataSet2);

        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);

        initChart();
    }

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

        String imgString = profile.getUserPicture().substring(22);
        byte[] imageBytes = Base64.decode(imgString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        img_picture.setImageBitmap(decodedImage);
    }

    double[][] distances = new double[8][2];
    double[][] avgSpeeds = new double[8][2];
    double[][] maxSpeeds = new double[8][2];
    int[][] times = new int[8][2];
    private void setGraphDatas(List<Stat> stats) {
        int i = 0;
        for(Stat stat : stats) {
            Log.d(TAG, stat.getWeek() + "주] 거리: " + stat.getDistance() + ", 평균속도" + stat.getAvgSpeed()
                    + ", 최고속도: " + stat.getMaxSpeed() + ", 시간: " + stat.getTime());
//            distances[i][0] = stat.getWeek();
//            avgSpeeds[i][0] = stat.getWeek();
//            maxSpeeds[i][0] = stat.getWeek();
//            times[i][0] = stat.getWeek();
//
//            distances[i][1] = stat.getDistance();
//            avgSpeeds[i][1] = stat.getAvgSpeed();
//            maxSpeeds[i][1] = stat.getMaxSpeed();
//            times[i][1] = stat.getTime();
//
//            i++;
        }
    }
}
