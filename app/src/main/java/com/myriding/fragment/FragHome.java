package com.myriding.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myriding.R;
import com.myriding.activity.HomeMapViewDetailActivity;
import com.myriding.activity.MyCourseMoreActivity;
import com.myriding.activity.SearchActivity;
import com.myriding.atapter.HomeRecyclerViewAdapter;
import com.myriding.atapter.RankRecyclerViewAdapter;
import com.myriding.http.RetrofitAPI;
import com.myriding.http.RetrofitClient;
import com.myriding.model.Home;
import com.myriding.model.HomeValue;
import com.myriding.model.HomeResponse;
import com.myriding.model.MongoValue;
import com.myriding.model.MysqlValue;
import com.myriding.model.RouteMongoValue;
import com.myriding.model.Token;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragHome extends Fragment implements OnMapReadyCallback {
    final static String TAG = "FragHome";

    DecimalFormat dataFormat = new DecimalFormat("#.##");

    private View view;

    private ConstraintLayout layout_record;
    private TextView tv_title;
    private TextView tv_startPoint;
    private TextView tv_endPoint;
    private TextView tv_distance;
    private TextView tv_time;
    private TextView tv_avgSpeed;
    private TextView tv_maxSpeed;
    private MapView mapView;
    private TextView btn_next;
    private TextView btn_prev;
    private View view_touch;

    GoogleMap mMap;

    MaterialCalendarView materialCalendarView;
    final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    int selectYear, selectMonth, selectDay;

    private List<Home> lstHome = new ArrayList<>();

    int currentPost = 0, totalPostNum = 0;
    int[] todayPostID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_home, container, false);

        initCalendar(view, savedInstanceState);

        layout_record = (ConstraintLayout) view.findViewById(R.id.home_record_layout);
        tv_title = (TextView) view.findViewById(R.id.post_title);
        tv_startPoint = (TextView) view.findViewById(R.id.post_start_point);
        tv_endPoint = (TextView) view.findViewById(R.id.post_end_point);
        tv_distance = (TextView) view.findViewById(R.id.post_distance);
        tv_time = (TextView) view.findViewById(R.id.post_time);
        tv_avgSpeed = (TextView) view.findViewById(R.id.post_speed_avg);
        tv_maxSpeed = (TextView) view.findViewById(R.id.post_speed_max);
        view_touch = (View) view.findViewById(R.id.home_view);
        view_touch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HomeMapViewDetailActivity.class);
                intent.putExtra("post_id", todayPostID[currentPost]);
                startActivity(intent);
            }
        });

        btn_next = (TextView) view.findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPost++;
                if(lstHome.get(currentPost).getArrayPoints() == null)
                    getRouteData(selectYear, selectMonth, selectDay, todayPostID[currentPost]);
                else
                    setPost();
            }
        });

        btn_prev = (TextView) view.findViewById(R.id.btn_prev);
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPost--;
                setPost();
            }
        });

        mapView = (MapView) view.findViewById(R.id.home_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 초기 데이터 획득
        selectYear = CalendarDay.today().getYear();
        selectMonth =  CalendarDay.today().getMonth() + 1;
        selectDay = CalendarDay.today().getDay();

        getOnedayRecord(selectYear, selectMonth, selectDay);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop () {
        super.onStop();
        mapView.onStop();

    }

    @Override
    public void onSaveInstanceState (@Nullable Bundle outState){
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);

    }

    private void initCalendar(final View view, final @Nullable Bundle savedInstanceState) {
        materialCalendarView = (MaterialCalendarView)view.findViewById(R.id.calendarView);

        // 달력 설정 (주 시작 요일, 달력 시작 범위, 달력 끝 범위, 달력 모드 = 주)
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2021, 0, 1))
                .setMaximumDate(CalendarDay.from(2030, 11, 31))
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();
        materialCalendarView.addDecorators(oneDayDecorator);

        // 날짜 선택 이벤트
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                totalPostNum = 0;
                currentPost = 0;
                todayPostID = null;

                // 선택된 날짜 정보 획득
                selectYear = date.getYear();
                selectMonth = date.getMonth() + 1;
                selectDay = date.getDay();
                getOnedayRecord(selectYear, selectMonth, selectDay);
            }
        });
    }

    public class OneDayDecorator implements DayViewDecorator {
        private CalendarDay date;

        public OneDayDecorator() {
            date = CalendarDay.today();
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return date != null && day.equals(date);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new StyleSpan(Typeface.BOLD));
            view.addSpan(new RelativeSizeSpan(1.4f));
            view.addSpan(new ForegroundColorSpan(Color.BLACK));
        }

        /**
         * We're changing the internals, so make sure to call {@linkplain MaterialCalendarView#invalidateDecorators()}
         */
        public void setDate(Date date) {
            this.date = CalendarDay.from(date);
        }
    }

    private RetrofitAPI retrofitAPI;
    private void getOnedayRecord(int year, int month, int day) {
        retrofitAPI = RetrofitClient.getApiService();

        Call<HomeResponse> call = retrofitAPI.getOnedayRecord(Token.getToken(), year, month, day);
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "라이딩 일지 조회 성공");

                    HomeValue homeValue = response.body().getHomeValue();

                    if(homeValue == null) {
                        Log.d(TAG, "라이딩 데이터 없음");
                        layout_record.setVisibility(View.INVISIBLE);
                    }

                    if(homeValue != null) {
                        Log.d(TAG, "라이딩 데이터 있음");
                        if (homeValue.getMysqlValue() != null) {
                            List<MysqlValue> mySqlValues = homeValue.getMysqlValue();
                            setPostData(mySqlValues);
                        }

                        if (homeValue.getMongoValue() != null) {
                            List<MongoValue> mongoValues = homeValue.getMongoValue();
                            setRouteData(mongoValues);
                        }

                        totalPostNum =  homeValue.getTodayValue().size();
                        todayPostID = new int[totalPostNum];
                        for(int i = 0; i < totalPostNum; i++) {
                            todayPostID[i] = homeValue.getTodayValue().get(i);
                        }

                        setPost();
                    }
                } else {
                    Log.d(TAG, "라이딩 일지 조회 실패");
                    layout_record.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                // Toast.makeText(getContext(), "서버 통신 실패", Toast.LENGTH_SHORT).show();
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private void getRouteData(int year, int month, int day, int record_id) {
        retrofitAPI = RetrofitClient.getApiService();

        Call<HomeResponse> call = retrofitAPI.getRecordRoute(Token.getToken(), year, month, day, record_id);
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "라이딩 일지 조회 성공");
                    HomeValue homeValue = response.body().getHomeValue();

                    if(homeValue == null) {
                        Log.d(TAG, "라이딩 데이터 없음");
                        layout_record.setVisibility(View.INVISIBLE);
                    }

                    if(homeValue != null) {
                        Log.d(TAG, "라이딩 데이터 있음");

                        if (homeValue.getMongoValue() != null) {
                            List<MongoValue> mongoValues = homeValue.getMongoValue();
                            setRouteData(mongoValues);
                        }

                        setPost();
                    }

                    if(homeValue == null) {
                        Log.d(TAG, "라이딩 데이터 없음");
                        layout_record.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Log.d(TAG, "라이딩 일지 조회 실패");
                    layout_record.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private void setPostData(List<MysqlValue> mysqlValues) {
        lstHome.clear();
        for(MysqlValue mysqlValue : mysqlValues) {
            lstHome.add(
                    new Home(
                            mysqlValue.getRecTitle(),
                            mysqlValue.getRecStartPointAddress(),
                            mysqlValue.getRecEndPointAddress(),
                            mysqlValue.getRecDistance(),
                            mysqlValue.getRecTime(),
                            mysqlValue.getRecAvgSpeed(),
                            mysqlValue.getRecMaxSpeed()
                    )
            );
        }
    }

    private void setRouteData(List<MongoValue> mongoValues) {
        ArrayList<LatLng> arrayPoints = new ArrayList<LatLng>();
        for(MongoValue mongoValue : mongoValues) {
            arrayPoints.add(new LatLng(mongoValue.getLat(), mongoValue.getLng()));
        }
        lstHome.get(currentPost).setArrayPoints(arrayPoints);
    }

    PolylineOptions polylineOptions;
    private void setPost() {
        tv_title.setText(lstHome.get(currentPost).getTitle());
        tv_startPoint.setText(lstHome.get(currentPost).getStartPoint());
        tv_endPoint.setText(lstHome.get(currentPost).getEndPoint());
        tv_distance.setText(dataFormat.format(lstHome.get(currentPost).getDistance()) + "km");
        tv_time.setText(lstHome.get(currentPost).getTime() + "");
        tv_avgSpeed.setText(dataFormat.format(lstHome.get(currentPost).getAvgSpeed()) + "km/h");
        tv_maxSpeed.setText(dataFormat.format(lstHome.get(currentPost).getMaxSpeed()) + "km/h");

        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(10);
        polylineOptions.addAll(lstHome.get(currentPost).getArrayPoints());

        mMap.clear();

        // 경로의 중간지점을 중심으로 카메라 줌
        LatLng startPosition = new LatLng(
                lstHome.get(currentPost).getArrayPoints().get(0).latitude,
                lstHome.get(currentPost).getArrayPoints().get(0).longitude
        );

        int size = lstHome.get(currentPost).getArrayPoints().size();
        LatLng endPosition = new LatLng(
                lstHome.get(currentPost).getArrayPoints().get(size - 1).latitude,
                lstHome.get(currentPost).getArrayPoints().get(size - 1).longitude
        );

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(startPosition).include(endPosition);
        LatLngBounds bounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 16));
        mMap.addPolyline(polylineOptions);

        if(layout_record.getVisibility() == View.INVISIBLE) layout_record.setVisibility(View.VISIBLE);

        // 총 페이지 개수가 1보다 크고 현재 번호가 총 페이지 - 1보다 작으면 next 버튼 보이기
        if(totalPostNum > 1 && currentPost < totalPostNum - 1) btn_next.setVisibility(View.VISIBLE);
        else btn_next.setVisibility(View.INVISIBLE);

        // 현재 번호가 1보다 크면 prev 버튼 보이기
        if(currentPost > 0) btn_prev.setVisibility(View.VISIBLE);
        else btn_prev.setVisibility(View.INVISIBLE);
    }

}
