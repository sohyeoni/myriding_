package com.myriding.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.myriding.R;
import com.myriding.atapter.SearchRecyclerViewAdapter;
import com.myriding.http.RetrofitAPI;
import com.myriding.http.RetrofitClient;
import com.myriding.model.CourseData;
import com.myriding.model.CourseResponse;
import com.myriding.model.Search;
import com.myriding.model.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    final static String TAG = "SearchActivity";

    final int SORT_RECENTLY = 1;
    final int SORT_LIKE_NUM = 2;
    final int SORT_DISTANCE = 3;
    final int SORT_TIME = 4;
    final int SORT_RIDING_NUM = 5;

    private EditText et_keyword;
    private ImageView img_loading;

    private RadioGroup sortRadioGroup;
    private RadioButton sortNew, sortLike, sortView, sortDistance, sortTime;

    private RecyclerView myRecyclerview;
    private SearchRecyclerViewAdapter recyclerAdapter;
    private List<Search> lstSearch = new ArrayList<>();

    private int sortValue = 1;
    private String searchWord = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        img_loading = (ImageView) findViewById(R.id.search_loading_image);

        et_keyword = (EditText) findViewById(R.id.search_keyword);
        et_keyword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_keyword.getWindowToken(), 0);

                    searchWord = et_keyword.getText().toString();
                    sortValue = SORT_RECENTLY;
                    if(sortRadioGroup.getCheckedRadioButtonId() != R.id.sort_new) {
                        sortNew.toggle();
                    } else {
                        Glide.with(getApplicationContext()).load(R.raw.gif_loading).into(img_loading);
                        getCourseSearchResult(searchWord, sortValue);
                    }

                    return true;
                }
                return false;
            }
        });
        et_keyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_keyword.setText("");
            }
        });

        // <-- Radio button
        sortNew = (RadioButton) findViewById(R.id.sort_new);
        sortLike = (RadioButton) findViewById(R.id.sort_like);
        sortView = (RadioButton) findViewById(R.id.sort_view);
        sortDistance = (RadioButton) findViewById(R.id.sort_distance);
        sortTime = (RadioButton) findViewById(R.id.sort_time);

        sortRadioGroup = (RadioGroup) findViewById(R.id.sortRadioGroup);
        sortRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.sort_new:
                        sortValue = SORT_RECENTLY;
                        changeRadioButtonTextColor(Color.WHITE, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK);
                        break;
                    case R.id.sort_like:
                        sortValue = SORT_LIKE_NUM;
                        changeRadioButtonTextColor(Color.BLACK, Color.WHITE, Color.BLACK, Color.BLACK, Color.BLACK);
                        break;
                    case R.id.sort_view:
                        sortValue = SORT_RIDING_NUM;
                        changeRadioButtonTextColor(Color.BLACK, Color.BLACK, Color.WHITE, Color.BLACK, Color.BLACK);
                        break;
                    case R.id.sort_distance:
                        sortValue = SORT_DISTANCE;
                        changeRadioButtonTextColor(Color.BLACK, Color.BLACK, Color.BLACK, Color.WHITE, Color.BLACK);
                        break;
                    case R.id.sort_time:
                        sortValue = SORT_TIME;
                        changeRadioButtonTextColor(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.WHITE);
                        break;
                }

                if(searchWord != "")
                    img_loading.setVisibility(View.VISIBLE);
                    getCourseSearchResult(searchWord, sortValue);
            }
        });
        // -->

        // <-- Search Course Post
        myRecyclerview = (RecyclerView) findViewById(R.id.search_recyclerview);
        myRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        // -->
    }

    private void changeRadioButtonTextColor(int sort_new, int sort_like, int sort_view, int sort_distance, int sort_time) {
        sortNew.setTextColor(sort_new);
        sortLike.setTextColor(sort_like);
        sortView.setTextColor(sort_view);
        sortDistance.setTextColor(sort_distance);
        sortTime.setTextColor(sort_time);
    }

    /* 라이딩 코스 검색 결과 획득 메서드 */
    private RetrofitAPI retrofitAPI;
    private void getCourseSearchResult(String keyword, int sortValue) {
        retrofitAPI = RetrofitClient.getApiService();

        Call<CourseResponse> call = retrofitAPI.getSearchCourse(Token.getToken(), keyword, sortValue);
        call.enqueue(new Callback<CourseResponse>() {
            @Override
            public void onResponse(Call<CourseResponse> call, Response<CourseResponse> response) {
                if(response.isSuccessful()) {
                    List<CourseData> searchResult = response.body().getRoutes();

                    setCourseSearchResultList(searchResult);

                    recyclerAdapter = new SearchRecyclerViewAdapter(SearchActivity.this, lstSearch);
                    myRecyclerview.setAdapter(recyclerAdapter);
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.d(TAG, "라이딩 코스 검색 실패");
                        Log.d(TAG, errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                img_loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<CourseResponse> call, Throwable t) {
                t.getMessage();
                Log.d(TAG, "라이딩 코스 검색 실패");
                Log.d(TAG, t.getMessage());

                img_loading.setVisibility(View.GONE);
            }
        });
    }

    /* 리사이클러뷰에 표시를 위해 획득한 코스 검색 결과를 리스트에 저장 */
    private void setCourseSearchResultList(List<CourseData> routes) {
        lstSearch.clear();
        for(CourseData route : routes) {
            lstSearch.add(
                    new Search(
                            route.getId(),
                            route.getRouteUserId(),
                            route.getRouteTitle(),
                            route.getRouteImage(),
                            route.getRouteDistance(),
                            route.getRouteTime(),
                            route.getRouteLike(),
                            route.getCreatedAt(),
                            route.getRouteStartPointAddress(),
                            route.getRouteEndPointAddress(),
                            route.getRouteNumOfTryCount()
                    )
            );
        }
    }
}