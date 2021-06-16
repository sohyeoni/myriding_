package com.myriding.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myriding.R;
import com.myriding.atapter.BadgeRecyclerViewAdapter;
import com.myriding.atapter.BadgeSectionViewAdapter;
import com.myriding.http.RetrofitAPI;
import com.myriding.http.RetrofitClient;
import com.myriding.model.Badge;
import com.myriding.model.BadgePreview;
import com.myriding.model.Token;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BadgeHomeActivity extends AppCompatActivity {
    private static final String TAG = "BadgeHomeActivity";

    private static final String[] DISTANCE_BADGE_NAMES = {"누적 30km 달성", "누적 50km 달성", "누적 100km 달성", "누적 150km 달성", "누적 300km 달성"};
    private static final String[] SPEED_BADGE_NAMES = {"최고 속도 15km/h 달성", "최고 속도 20km/h 달성", "최고 속도 25km/h 달성", "최고 속도 30km/h 달성", "최고 속도 50km/h 달성"};
    private static final String[] TIME_BADGE_NAMES = {"누적 5시간 달성", "누적 10시간 달성", "누적 20시간 달성", "누적 30시간 달성", "누적 50시간 달성"};
    private static final int BADGE_MAX_NUM = 5;
    private static final int TOTAL_BADGE_MAX_NUM = 15;

    private RecyclerView mRecyclerView;
    private BadgeRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge);

        Intent intent = getIntent();
        int[] badgeCounts = intent.getExtras().getIntArray("badgeCount");

        getBadge();

        mRecyclerView = (RecyclerView) findViewById(R.id.badge_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        mAdapter = new BadgeRecyclerViewAdapter(this);
        /*for(int i = 0; i < TOTAL_BADGE_MAX_NUM; i++) {
            if(i < 5) {
                mAdapter.addItem(i, new Badge(R.drawable.img_badge_distance, "Badge" + i));
            } else if(i < 10) {
                mAdapter.addItem(i, new Badge(R.drawable.img_badge_speed, "Badge" + i));
            } else {
                mAdapter.addItem(i, new Badge(R.drawable.img_badge_time, "Badge" + i));
            }
        }*/

        for(int i = 0; i < BADGE_MAX_NUM; i++) {
            if(i < badgeCounts[0]) {
                mAdapter.addItem(i, new Badge(R.drawable.img_badge_distance, DISTANCE_BADGE_NAMES[i]));
            } else {
                mAdapter.addItem(i, new Badge(R.drawable.img_badge_distance_no, DISTANCE_BADGE_NAMES[i]));
            }
        }

        for(int i = 0; i < BADGE_MAX_NUM; i++) {
            if(i < badgeCounts[1]) {
                mAdapter.addItem(i + 5, new Badge(R.drawable.img_badge_speed, SPEED_BADGE_NAMES[i]));
            } else {
                mAdapter.addItem(i + 5, new Badge(R.drawable.img_badge_speed_no, SPEED_BADGE_NAMES[i]));
            }
        }

        for(int i = 0; i < BADGE_MAX_NUM; i++) {
            if(i < badgeCounts[2]) {
                mAdapter.addItem(i + 10, new Badge(R.drawable.img_badge_time, TIME_BADGE_NAMES[i]));
            } else {
                mAdapter.addItem(i + 10, new Badge(R.drawable.img_badge_time_no, TIME_BADGE_NAMES[i]));
            }
        }

        List<BadgeSectionViewAdapter.Section> sections = new ArrayList<BadgeSectionViewAdapter.Section>();

        sections.add(new BadgeSectionViewAdapter.Section(0, "DISTANCE"));
        sections.add(new BadgeSectionViewAdapter.Section(5, "SPEED"));
        sections.add(new BadgeSectionViewAdapter.Section(10, "TIME"));
        // sections.add(new BadgeSectionViewAdapter.Section(20, "PERIOD"));
        // sections.add(new BadgeSectionViewAdapter.Section(25, "SCORE"));

        BadgeSectionViewAdapter.Section[] dummy = new BadgeSectionViewAdapter.Section[sections.size()];
        BadgeSectionViewAdapter mSectionAdapter = new BadgeSectionViewAdapter(this, R.layout.badge_section, R.id.tv_section, mRecyclerView, mAdapter);
        mSectionAdapter.setSections(sections.toArray(dummy));

        mRecyclerView.setAdapter(mSectionAdapter);
    }

    private RetrofitAPI retrofitAPI;
    private void getBadge() {
        retrofitAPI = RetrofitClient.getApiService();

        Call<JSONObject> call = retrofitAPI.getBadge(Token.getToken());
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if(response.isSuccessful()) {

                } else {

                }

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Log.d(TAG, gson.toJson(response.body()));
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                t.getMessage();
                Log.d(TAG, "라이딩 코스 검색 실패2");
                Log.d(TAG, t.getMessage());
            }
        });
    }
}