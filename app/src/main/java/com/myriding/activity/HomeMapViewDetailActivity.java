package com.myriding.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.myriding.R;

public class HomeMapViewDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_map_view_detail);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.home_map_detail);
        mapFragment.getMapAsync(this);
    }

    // 구글맵 띄울 준비가 되면 자동 호출
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        // 지도타입 = 일반
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // 서울 여의도로 위치 설정
        LatLng seoul = new LatLng(37.52487, 126.92723);

        // 카메라를 여의도 위치로 옮김 + 줌
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 16));
    }
}