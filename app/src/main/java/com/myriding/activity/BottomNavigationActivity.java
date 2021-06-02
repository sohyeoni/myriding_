package com.myriding.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.myriding.R;
import com.myriding.fragment.FragCourse;
import com.myriding.fragment.FragHome;
import com.myriding.fragment.FragProfile;
import com.myriding.fragment.FragRank;
import com.myriding.fragment.FragRiding;

public class BottomNavigationActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private FragHome fragHome;
    private FragCourse fragCourse;
    private FragRiding fragRiding;
    private FragRank fragRank;
    private FragProfile fragProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        // 하단 네비게이션의 각 아이콘 클릭 시 해당 프래그먼트로 이동
        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.home:
                        setFrag("Home");
                        break;
                    case R.id.course:
                        setFrag("Course");
                        break;
                    case R.id.riding:
                        // Intent intent = new Intent(getApplicationContext(), RidingMain2.class);
                        // startActivity(intent);
                        break;
                    case R.id.rank:
                        setFrag("Rank");
                        break;
                    case R.id.profile:
                        setFrag("Profile");
                        break;
                }
                return true;
            }
        });

        fragHome    = new FragHome();
        fragCourse  = new FragCourse();
        fragRiding  = new FragRiding();
        fragRank    = new FragRank();
        fragProfile = new FragProfile();

        // 첫 프래그먼트 화면 지정
        setFrag("Home");
    }

    // 프래그먼트 교체 함수
    private void setFrag(String fragName) {
        fragmentManager     = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (fragName) {
            case "Home":
                fragmentTransaction.replace(R.id.main_frame, fragHome);
                fragmentTransaction.commit();
                break;
            case "Course":
                fragmentTransaction.replace(R.id.main_frame, fragCourse);
                fragmentTransaction.commit();
                break;
            case "Riding":
                fragmentTransaction.replace(R.id.main_frame, fragRiding);
                fragmentTransaction.commit();
                break;
            case "Rank":
                fragmentTransaction.replace(R.id.main_frame, fragRank);
                fragmentTransaction.commit();
                break;
            case "Profile":
                fragmentTransaction.replace(R.id.main_frame, fragProfile);
                fragmentTransaction.commit();
                break;
        }
    }
}