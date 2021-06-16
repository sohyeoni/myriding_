package com.myriding.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;
import com.myriding.R;
import com.myriding.http.RetrofitAPI;
import com.myriding.http.RetrofitClient;
import com.myriding.model.HomeDetailResponse;
import com.myriding.model.HomeResponse;
import com.myriding.model.HomeValue;
import com.myriding.model.MongoValue;
import com.myriding.model.Path;
import com.myriding.model.RouteMongoValue;
import com.myriding.model.Token;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeMapViewDetailActivity extends AppCompatActivity implements OnMapReadyCallback, Serializable {
    final static String TAG = "HomeMapViewDetailActivity";

    GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_map_view_detail);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.home_map_detail);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        int postId = intent.getExtras().getInt("post_id");

        getRouteData(postId);
    }

    // 구글맵 띄울 준비가 되면 자동 호출
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
    }

    private RetrofitAPI retrofitAPI;
    private void getRouteData(int post_id) {
        retrofitAPI = RetrofitClient.getApiService();

        Call<HomeDetailResponse> call = retrofitAPI.getDetailRoute(Token.getToken(), post_id);
        call.enqueue(new Callback<HomeDetailResponse>() {
            @Override
            public void onResponse(Call<HomeDetailResponse> call, Response<HomeDetailResponse> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "라이딩 일지 조회 성공");
                    List<Path> paths = response.body().getRecordValue().getPath();
                    if(paths != null) {
                        setRoutes(paths);
                    }
                } else {
                    Log.d(TAG, "라이딩 일지 조회 실패");
                }
            }

            @Override
            public void onFailure(Call<HomeDetailResponse> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    PolylineOptions polylineOptions = new PolylineOptions();
    ArrayList<LatLng> arrayPoints = new ArrayList<LatLng>();
    void setRoutes(List<Path> routes) {
        for(Path route : routes) {
            arrayPoints.add(new LatLng(route.getLat(), route.getLng()));
        }

        int center = (int) (routes.size() / 2);
        LatLng latLng = new LatLng(routes.get(center).getLat(), routes.get(center).getLng());

        polylineOptions.color(Color.RED);
        polylineOptions.width(10);
        polylineOptions.addAll(arrayPoints);

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        mGoogleMap.addPolyline(polylineOptions);

        // 경로의 중간지점을 중심으로 카메라 줌
        LatLng startPosition = new LatLng(
                arrayPoints.get(0).latitude, arrayPoints.get(0).longitude
        );

        int size = arrayPoints.size();
        LatLng endPosition = new LatLng(
                arrayPoints.get(size - 1).latitude, arrayPoints.get(size - 1).longitude
        );

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(startPosition).include(endPosition);
        LatLngBounds bounds = builder.build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 16));
        mGoogleMap.addPolyline(polylineOptions);
    }
}