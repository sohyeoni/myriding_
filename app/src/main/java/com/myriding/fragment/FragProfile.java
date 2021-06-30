package com.myriding.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.myriding.PreferenceManager;
import com.myriding.R;
import com.myriding.activity.BadgeHomeActivity;
import com.myriding.activity.LoginActivity;
import com.myriding.atapter.ProfileRecyclerViewAdapter;
import com.myriding.http.RetrofitAPI;
import com.myriding.http.RetrofitClient;
import com.myriding.model.BadgePreview;
import com.myriding.model.Profile;
import com.myriding.model.ProfileResponse;
import com.myriding.model.Stat;
import com.myriding.model.Token;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragProfile extends Fragment {
    private static final String TAG = "FragProfile";
    private static final String DISTANCE_CHART_COLOR = "#FFB85A";
    private static final String TIME_CHART_COLOR = "#9FC93C";
    private static final String AVG_SPEED_CHART_COLOR = "#1266FF";
    private static final String MAX_SPEED_CHART_COLOR = "#C98AFF";
    private static final int UNCLICKED_BUTTON_COLOR = Color.parseColor("#d3d3d3");
    private static final int PICK_FROM_ALBUM = 1;

    DecimalFormat scoreFormat = new DecimalFormat("#,###");

    private View view;

    private RadioButton rb_Left, rb_Right, rb_Center;
    private RadioGroup radioGroup;

    private ImageView img_picture, img_loading;
    private TextView tv_username, tv_score, tv_count, btn_logout;

    private LineChart chart;
    List<Entry> distances = new ArrayList<>();
    List<Entry> avgSpeeds = new ArrayList<>();
    List<Entry> maxSpeeds = new ArrayList<>();
    List<Entry> times = new ArrayList<>();

    private TextView tv_badgeMore;
    private RecyclerView myrecyclerview;

    private ProfileRecyclerViewAdapter recyclerAdapter;
    private List<BadgePreview> lstBadge;

    int[] numOfBadges;

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

        btn_logout = (TextView) view.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        img_loading = (ImageView) view.findViewById(R.id.profile_loading_image);
        Glide.with(this).load(R.raw.gif_loading).into(img_loading);

        img_picture = (ImageView) view.findViewById(R.id.profile_img);
        img_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSelfPermission();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });

        // Glide.with(this).load("http://goo.gl/gEgYUd").into(img_picture);

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
                Intent intent = new Intent(getActivity(), BadgeHomeActivity.class);

                // int[] values = new int[] {0, 5, 10, 15, 20};
                intent.putExtra("badgeCount", numOfBadges);

                startActivity(intent);
            }
        });
        // -->

        // <-- 배찌 목록 생성
        myrecyclerview = (RecyclerView) view.findViewById(R.id.profile_recyclerview);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        // -->

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lstBadge = new ArrayList<>();
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

                        lstBadge.add(new BadgePreview("DISTANCE", profile.getBadge().getDistanceBadge(), R.drawable.img_badge_distance));
                        lstBadge.add(new BadgePreview("SPEED", profile.getBadge().getMaxSpeedBadge(), R.drawable.img_badge_speed));
                        lstBadge.add(new BadgePreview("TIME", profile.getBadge().getTimeBadge(), R.drawable.img_badge_time));

                        numOfBadges = new int[lstBadge.size()];
                        numOfBadges[0] = profile.getBadge().getDistanceBadge();
                        numOfBadges[1] = profile.getBadge().getMaxSpeedBadge();
                        numOfBadges[2] = profile.getBadge().getTimeBadge();

                        recyclerAdapter = new ProfileRecyclerViewAdapter(getContext(), lstBadge);
                        myrecyclerview.setAdapter(recyclerAdapter);

                        img_loading.setVisibility(View.GONE);
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
        tv_score.setText(scoreFormat.format(profile.getUserScoreOfRiding()) + "점");
        tv_count.setText(scoreFormat.format(profile.getUserNumOfRiding()) + "회");

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

    //권한에 대한 응답이 있을때 작동하는 함수
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //권한을 허용 했을 경우
        if (requestCode == 1) {
            int length = permissions.length;
            for (int i = 0; i < length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // 동의
                    Log.d("MainActivity", "권한 허용 : " + permissions[i]);
                }
            }
        }
    }

    public void checkSelfPermission() {
        String temp = "";
        //파일 읽기 권한 확인
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
        }

        //파일 쓰기 권한 확인
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
        }

        if (TextUtils.isEmpty(temp) == false) {
            // 권한 요청
            ActivityCompat.requestPermissions(getActivity(), temp.trim().split(" "),1);
        }else {
            // 모두 허용 상태
            Toast.makeText(getContext(), "권한을 모두 허용", Toast.LENGTH_SHORT).show();
        }
    }

    Uri imgUri;
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            try {
                InputStream is = getActivity().getContentResolver().openInputStream(data.getData());
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), getBytes(is));
                MultipartBody.Part body = MultipartBody.Part.createFormData("user_picture", "image.jpg", requestFile);
                is.close();

                imgUri = data.getData();

                updateProfileImage(body);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 1 && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getContext(), "취소", Toast.LENGTH_SHORT).show();
        }
    }

    public byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream byteBuff = new ByteArrayOutputStream();

        int buffSize = 1024;
        byte[] buff = new byte[buffSize];

        int len = 0;
        while ((len = is.read(buff)) != -1) {
            byteBuff.write(buff, 0, len);
        }

        return byteBuff.toByteArray();
    }

    private void updateProfileImage(MultipartBody.Part imageFile) {
        retrofitAPI = RetrofitClient.getApiService();

        Call<JSONObject> call = retrofitAPI.uploadProfileImage(Token.getToken(), imageFile);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "저장 성공");
                    img_picture.setImageURI(imgUri);
                } else {
                    Log.d(TAG, "저장 실패");
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.d(TAG, "저장 실패2");
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private void logout() {
        retrofitAPI = RetrofitClient.getApiService();

        Call<JSONObject> call = retrofitAPI.logout(Token.getToken());
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                PreferenceManager.clear(getContext());

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.d(TAG, t.getMessage());

                PreferenceManager.clear(getContext());

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
