package com.myriding.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.myriding.CustomView;
import com.myriding.R;
import com.myriding.http.RetrofitAPI;
import com.myriding.http.RetrofitClient;
import com.myriding.http.RetrofitWeather;
import com.myriding.http.WeatherAPI;
import com.myriding.http.WeatherApiKey;
import com.myriding.model.CourseDetailResponse;
import com.myriding.model.Record;
import com.myriding.model.RouteMongoValue;
import com.myriding.model.RouteValue;
import com.myriding.model.Token;
import com.myriding.model.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RidingActivity extends AppCompatActivity implements Button.OnClickListener {
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    boolean isUseBackPhone = false;
    final static String TAG = "RidingActivity";
    final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean showGraphics = false;

    private ConstraintLayout recordLayout;
    private LinearLayout miniRecordLayout;
    private LinearLayout weatherLayout;
    private LinearLayout sensorLayout;

    private LinearLayout startLinearLayout;
    private LinearLayout pauseLinearLayout;
    private LinearLayout restartStopLinearLayout;

    private Button btn_showMap;
    private Button btn_showRecord;
    private Button btn_showSensor;
    private ImageButton btn_left;
    private ImageButton btn_right;
    // private GoogleMap mGoogleMap;

    private boolean isRiding = false;
    private Thread timeThread = null;
    private RidingActivity.TimeHandler timeHandler;

    private TextView tv_miniRecTime;
    private TextView tv_recTime;
    private TextView tv_miniRecDistance;
    private TextView tv_recDistance;
    private TextView tv_recSpeed;
    private TextView tv_miniRecSpeed;
    private TextView tv_recMaxSpeed;
    private TextView tv_sensorValue;
    private TextView tv_recAvgSpeed;
    private TextView tv_currentLocation, tv_currentTemp, tv_currentWind;
    private CustomView customView;

    Location lastLocation = null;
    List<Polyline> polylines = new ArrayList<>();
    List<Record> myRecords;
    List<Point> myPoints;

    double totalDistance = 0.0;
    double maxSpeed = 0.0;

    private String myIP;
    private String clientIP;
    Dialog myDialog;
    Thread thread;
    private String sensorValue;
    Handler graphicHandler;
    Integer courseID = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riding);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("course_id")) {
            courseID = intent.getExtras().getInt("course_id");
        }

        init();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.riding_map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.setMyLocationEnabled(true);

                Location location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
                currentLocationAddress = getCurrentAddress(location.getLatitude(), location.getLongitude());
                tv_currentLocation.setText(currentLocationAddress);
                getWeather(location.getLatitude(), location.getLongitude());

                if(courseID != null) {
                    getDetailCourse(courseID);
                }
            }
        });

        try {
            MapsInitializer.initialize(this);
        } catch(Exception e) {
            e.printStackTrace();
        }

        startLocationService();

        // 후방 알림 사용여부 확인 후 연결 작업 실시
        showUseBackPhone();


        if(isUseBackPhone) {
            graphicHandler = new Handler();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    getBackOjbectDistance();
                    graphicHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (showGraphics) {
                                customView.invalidate();
                            }
                        }
                    });
                }
            });
            t.start();
        }
    }

    void init() {
        miniRecordLayout = (LinearLayout) findViewById(R.id.riding_mini_record_layout);
        weatherLayout = (LinearLayout) findViewById(R.id.riding_mini_weather_layout);
        recordLayout = (ConstraintLayout) findViewById(R.id.riding_record_layout);
        sensorLayout = (LinearLayout) findViewById(R.id.sensorLayout);

        startLinearLayout = (LinearLayout) findViewById(R.id.start_button_layout);
        pauseLinearLayout = (LinearLayout) findViewById(R.id.pause_button_layout);
        restartStopLinearLayout = (LinearLayout) findViewById(R.id.restart_stop_button_layout);

        btn_showMap = (Button) findViewById(R.id.show_map_button);
        btn_showRecord = (Button) findViewById(R.id.show_record_button);
        btn_showSensor = (Button) findViewById(R.id.show_sensor_button);
        btn_showMap.setOnClickListener(this);
        btn_showRecord.setOnClickListener(this);
        btn_showSensor.setOnClickListener(this);
        btn_left = (ImageButton) findViewById(R.id.riding_left_button);
        btn_right = (ImageButton) findViewById(R.id.riding_right_button);

        tv_recTime = (TextView) findViewById(R.id.riding_current_time);
        tv_miniRecTime = (TextView) findViewById(R.id.riding_mini_rec_time);
        tv_miniRecDistance = (TextView) findViewById(R.id.riding_mini_rec_distance);
        tv_recDistance = (TextView) findViewById(R.id.riding_current_distance);
        tv_recSpeed = (TextView) findViewById(R.id.riding_current_speed);
        tv_miniRecSpeed = (TextView) findViewById(R.id.riding_mini_rec_speed);
        tv_recMaxSpeed = (TextView) findViewById(R.id.riding_speed_max);
        tv_recAvgSpeed = (TextView) findViewById(R.id.riding_speed_avg_time);
        // tv_sensorValue = (TextView) findViewById(R.id.sensor_value);
        customView = (CustomView) findViewById(R.id.customView);

        tv_currentLocation = (TextView) findViewById(R.id.riding_current_location);
        tv_currentTemp = (TextView) findViewById(R.id.riding_current_weather);
        tv_currentWind = (TextView) findViewById(R.id.riding_current_wind);
    }

    private GoogleMap map;
    private SupportMapFragment mapFragment;
    final int REQUEST_PERMISSION_CODE = 1;
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    boolean checkPermissions() {
        for(String permission : PERMISSIONS) {
            if(ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    LocationManager manager;
    GPSListener gpsListener;
    public void startLocationService() {
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(!(manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
            showLocationServiceAlert();
        }

        try {
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                String message = "최근위치 " + latitude + ", " + longitude;
                currentLocationAddress = getCurrentAddress(latitude, longitude);

                Log.d(TAG, message);
            }
            /*gpsListener = new GPSListener();
            long minTime = 1000;
            float minDistance = 0;

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            Toast.makeText(getApplicationContext(), "내 위치 확인 요청함", Toast.LENGTH_SHORT).show();*/
        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    private String getCurrentAddress(double lat, double lon) {
        final Geocoder geocoder = new Geocoder(RidingActivity.this);
        List<Address> list = null;
        String currentLocationAddress = "";
        try {
            // double d1 = Double.parseDouble(et1.getText().toString());
            // double d2 = Double.parseDouble(et2.getText().toString());

            list = geocoder.getFromLocation(
                    lat - 0.00033974, // 위도
                    lon + 0.00004262, // 경도
                    1); // 얻어올 값의 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
        }
        if (list != null) {
            if (list.size()==0) {
                Log.d(TAG, "해당되는 주소 정보는 없습니다");
            } else {
                // Log.d(TAG + "[geocoder] : ", list.get(0).toString());
                String[] address = list.get(0).getAddressLine(0).split(" ");
                currentLocationAddress = address[1] + " " + address[2] + " " + address[3];
                // currentLocationAddress = list.get(0).getAdminArea() + " " + list.get(0).getLocality() + " " + list.get(0).getThoroughfare();
                // Log.d(TAG, currentLocationAddress);
            }
        }

        return currentLocationAddress;
    }

    double distanceTemp;
    double speedTemp;
    double avgSpeedTemp;
    String currentLocationAddress;
    String startPointAddress;
    String endPointAddress;
    class GPSListener implements LocationListener {
        MarkerOptions myLocationMarker;

        @Override
        public void onLocationChanged(@NonNull Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            // String message = "내 위치 " + latitude + ", " + longitude;
            // Log.d(TAG, message);

            currentLocationAddress = getCurrentAddress(latitude, longitude);
            if(startPointAddress == null) {
                startPointAddress = currentLocationAddress;
                Log.d(TAG + "Start", startPointAddress);
            } else {
                endPointAddress = currentLocationAddress;
                Log.d(TAG + "End", endPointAddress);
            }
            showCurrentLocation(latitude, longitude);

            if(lastLocation != null && totalTime % 300 == 0)
                getWeather(latitude, longitude);

            if(isRiding) {
                if(lastLocation != null) {
                    // 라이딩 거리
                    totalDistance = totalDistance + lastLocation.distanceTo(location);
                    distanceTemp = totalDistance / 1000.0;
                }

                // 현재 속도
                speedTemp = location.getSpeed() * (3600 / 1000);

                // 최고 속도
                maxSpeed = (maxSpeed < speedTemp) ? speedTemp : maxSpeed;

                // 평균 속도
                avgSpeedTemp = 0.0;
                if(totalTime > 0 && totalDistance > 0 && maxSpeed > 0) {
                    avgSpeedTemp = (totalDistance / totalTime) * 3.6;
                }

                lastLocation = location;
            }
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        private void showCurrentLocation(Double latitude, Double longitude) {
            LatLng curPoint = new LatLng(latitude, longitude);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 17));

            int updateTime = totalTime;

            if(lastLocation != null && isRiding && updateTime % 3 == 0)
                drawDirections(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), curPoint);
        }

        /* 폴리라인 그리기 */
        public void drawDirections(LatLng startPoint, LatLng endPoint) {
            if(myRecords.size() > 0) {
                LatLng startPoint1 = new LatLng(myRecords.get((myRecords.size() - 1)).getLat(), myRecords.get((myRecords.size() - 1)).getLng());
                PolylineOptions polylineOptions = new PolylineOptions()
                        .add(startPoint1)
                        .add(endPoint)
                        .color(Color.rgb(36, 120, 255))
                        .width(18)
                        .geodesic(true);
                polylines.add(map.addPolyline(polylineOptions));
            }

            Date dateTemp = new Date();
            myRecords.add(new Record(dateTemp, endPoint.latitude, endPoint.longitude, speedTemp));

            /*for(Record myRecord : myRecords) {
                Log.d(TAG + "[record]", myRecord.getDate2() + ", " + myRecord.getLat() + ", " + myRecord.getLng() + ", " + myRecord.getSpeed());
            }
            Log.d(TAG + "[record]",  "----------------------");*/
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();

        if(!checkPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE);
        }

        if(map != null) {
            map.setMyLocationEnabled(true);
        }

        if(mapFragment != null)
            mapFragment.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(timeHandler != null)
            timeHandler.removeMessages(0);

        isRiding = false;

        if(mapFragment != null)
            mapFragment.onDestroy();

        if(manager != null && gpsListener != null)
            manager.removeUpdates(gpsListener);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onPause() {
        super.onPause();

        if(map != null) {
            map.setMyLocationEnabled(false);
        }

        if(mapFragment != null)
            mapFragment.onPause();
    }

    @SuppressLint("MissingPermission")
    public void clickStart(View view) {
        btn_showRecord.setVisibility(View.VISIBLE);
        miniRecordLayout.setVisibility(View.VISIBLE);

        isRiding = true;

        myRecords = new ArrayList<>();
        myPoints = new ArrayList<>();

        gpsListener = new GPSListener();
        long minTime = 1000;
        float minDistance = 0;
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);

        timeThread = new Thread(new RidingActivity.TimeThread());
        timeHandler = new TimeHandler();
        timeThread.start();

        clickRestart(view);
    }

    public void clickPause(View view) {
        /*thread = new Thread(){
            public void run(){
                sendSignaltoBackPhone("P");
            }
        };
        thread.start();*/

        startLinearLayout.setVisibility(View.GONE);
        pauseLinearLayout.setVisibility(View.GONE);
        restartStopLinearLayout.setVisibility(View.VISIBLE);

        isRiding = false;
    }

    boolean isFirst = true;
    public void clickRestart(View view) {
        startLinearLayout.setVisibility(View.GONE);
        pauseLinearLayout.setVisibility(View.VISIBLE);
        restartStopLinearLayout.setVisibility(View.GONE);

        if(isUseBackPhone) {
            if (!isFirst) {
                thread = new Thread() {
                    public void run() {
                        //if(lastSignal == "S")
                        Log.d(TAG, "START");
                        sendSignaltoBackPhone("S");
                    }
                };
                thread.start();
            } else {
                isFirst = false;
            }
        }

        if(isRiding == false)
            isRiding = true;
    }

    // TODO: 거리와 시간 확인 후 저장 dialog 또는 삭제 / 종료 dialog
    public void clickStop(View view) {
        endDialog();
    }

    public void clickLeftSignal(View view) {
        thread = new Thread(){
            public void run(){
                //if(lastSignal == "S")
                sendSignaltoBackPhone("L");
            }
        };
        thread.start();

    }

    public void clickRightSignal(View view) {
        thread = new Thread(){
            public void run(){
                //if(lastSignal == "S")
                sendSignaltoBackPhone("R");
            }
        };
        thread.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.show_record_button:
                btn_showRecord.setVisibility(View.GONE);
                miniRecordLayout.setVisibility(View.GONE);
                weatherLayout.setVisibility(View.VISIBLE);
                recordLayout.setVisibility(View.VISIBLE);
                if(isUseBackPhone) {
                    btn_showSensor.setVisibility(View.VISIBLE);
                    showGraphics = false;
                } else {
                    btn_showMap.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.show_map_button:
                btn_showMap.setVisibility(View.GONE);
                miniRecordLayout.setVisibility(View.VISIBLE);
                btn_showRecord.setVisibility(View.VISIBLE);
                if(isUseBackPhone) {
                    sensorLayout.setVisibility(View.GONE);
                    showGraphics = false;
                } else {
                    weatherLayout.setVisibility(View.GONE);
                    recordLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.show_sensor_button:
                btn_showSensor.setVisibility(View.GONE);
                btn_showMap.setVisibility(View.VISIBLE);
                weatherLayout.setVisibility(View.GONE);
                recordLayout.setVisibility(View.GONE);
                sensorLayout.setVisibility(View.VISIBLE);
                //setContentView(new CustomView(this, 13, 13, 13));
                showGraphics = true;
                break;
        }
    }

    class TimeThread implements Runnable {
        @Override
        public void run() {
            int time = 0;

            while(!Thread.currentThread().isInterrupted()) {
                while(isRiding) {
                    Message msg = new Message();

                    // 시간 +1
                    msg.arg1 = time++;
                    timeHandler.sendMessage(msg);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    private int totalTime;
    private String lastSignal = "";
    class TimeHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // 주행 시간 업데이트
            int hours = msg.arg1 / 3600;
            int minutes = (msg.arg1 % 3600) / 60;
            int secs = msg.arg1 % 60;

            String time = String.format("%02d:%02d:%02d", hours, minutes, secs);
            totalTime = msg.arg1;
            tv_miniRecTime.setText(time);
            tv_recTime.setText(time);

            tv_miniRecDistance.setText(String.format("%.2f", distanceTemp));
            tv_recDistance.setText(String.format("%.2f", distanceTemp));
            tv_miniRecSpeed.setText(String.format("%.1f", speedTemp));
            tv_recSpeed.setText(String.format("%.1f", speedTemp));
            tv_recMaxSpeed.setText(String.format("%.1f", maxSpeed));
            tv_recAvgSpeed.setText(String.format("%.1f", avgSpeedTemp));

            tv_currentLocation.setText(currentLocationAddress);
            tv_currentTemp.setText(String.format("%.0f", currentTemp) + "℃");
            tv_currentWind.setText("풍속 " + String.format("%.0f", currentWindSpeed) + "m/s");
            // tv_currentWind.setText(parseDegree(currentWindDeg) + " " + currentWindSpeed + "m/s");

            // 후방 연결 변경사항
            if(isUseBackPhone) {
                thread = new Thread() {
                    public void run() {
                        if(speedTemp < 1 && lastSignal != "P") {
                        // if (speedTemp < 1) {
                            sendSignaltoBackPhone("P");
                            lastSignal = "P";
                        } else if(speedTemp > 1 && lastSignal != "S" && isRiding == true) {
                        // } else if (speedTemp > 1) {
                            sendSignaltoBackPhone("S");
                            lastSignal = "S";
                        }
                    }
                };
                thread.start();
            }


        }
    }

    /* 위치 서비스 사용 알림창 */
    private void showLocationServiceAlert ()  {
        final AlertDialog.Builder dialog = new AlertDialog.Builder ( this );
        dialog.setMessage ("위치 서비스를 사용하지 않고 있습니다." + "\n사용하여 활동을 기록하시겠습니까?" )
                .setPositiveButton ("확인", new DialogInterface.OnClickListener () {
                    @Override
                    public  void  onClick (DialogInterface paramDialogInterface, int paramInt)  {
                        Intent myIntent = new Intent (Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity (myIntent);
                    }
                })
                .setNegativeButton ( "취소" , new DialogInterface.OnClickListener () {
                    @Override
                    public  void  onClick (DialogInterface paramDialogInterface, int paramInt)  {
                    }
                });
        dialog.show ();
    }

    /* 후방 휴대폰 사용 여부 확인창 */
    private void showUseBackPhone() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this );
        dialog.setMessage("후방 휴대폰을 사용하여 위험 요소 알림을 받으시겠습니까?")
                .setPositiveButton("사용", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isUseBackPhone = true;
                        //if(isUseBackPhone) {
                        connectBackPhone();
                        //}
                    }
                })
                .setNegativeButton("사용 안함", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isUseBackPhone = false;
                        btn_left.setVisibility(View.GONE);
                        btn_right.setVisibility(View.GONE);
                    }
                });
        dialog.show();
    }

    /* 라이딩 기록에 따른 dialog 출력 메서드 */
    private void endDialog() {
        // TODO 후방에 종료 신호 주기 -> 후방에서는 블루투스 및 소켓 대기 종료 작업
        final EditText et_ridingName = new EditText(RidingActivity.this);
        AlertDialog.Builder dialog = new AlertDialog.Builder(RidingActivity.this);

        dialog.setMessage("라이딩 명");
        dialog.setView(et_ridingName);
        dialog.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String title = et_ridingName.getText().toString();

                int time = (int)(totalTime / 60);
                Log.d("time", time + "");

                // oject array를 json으로 변경
                JSONArray myRecordForJson = new JSONArray();
                try {
                    for(int iCount = 0; iCount < myRecords.size(); iCount++) {
                        JSONObject object = new JSONObject();
                        object.put("date", myRecords.get(iCount).getDate());
                        object.put("lat", myRecords.get(iCount).getLat());
                        object.put("lng", myRecords.get(iCount).getLng());
                        object.put("speed", myRecords.get(iCount).getSpeed());
                        myRecordForJson.put(object);
                    }

                    Log.d(TAG, myRecordForJson.toString());
                } catch(JSONException e) {
                    e.printStackTrace();
                }

                Log.d(TAG + "[RECORD] : ", distanceTemp + ", " + avgSpeedTemp + ", " + maxSpeed);
                Log.d(TAG + "[RECORD] : ", (myRecordForJson instanceof JSONArray) ? "true" : "false");

                setMyRecord(title, distanceTemp, time, startPointAddress, endPointAddress, avgSpeedTemp, maxSpeed, courseID, myRecordForJson);

                dialogInterface.dismiss();
            }
        });

        dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "1";
    }

    /* 후방 휴대폰의 IP를 얻기 위한 소켓통신 메서드 (이거 사용) */
    class SignalThread extends Thread {
        @Override
        public void run() {
            int port = 10000;

            try {
                ServerSocket server = new ServerSocket(port);

                Socket socket = server.accept();
                InetAddress clientHost = socket.getLocalAddress();
                int clientPort = socket.getPort();
                printServerLog("클라이언트 연결됨 : " + clientHost + " : " + clientPort);

                ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
                Object obj = instream.readObject();
                printServerLog("데이터 받음 : " + obj);

                ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
                outstream.writeObject(obj + " from Server.");
                outstream.flush();
                printServerLog("데이터 보냄");
                printServerLog(obj.toString());
                clientIP = obj.toString();
                myDialog.dismiss();
                isUseBackPhone = true;
                Log.d("thread", "signal thread");
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* 방향지시등 데이터를 보내기 위해 QR코드를 이용하여 후방 휴대폰의 IP 획득 메서드 */
    private void connectBackPhone() {
        myDialog = new Dialog(RidingActivity.this);
        myDialog.setContentView(R.layout.riding_qrcord);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView img_qrcode = (ImageView) myDialog.findViewById(R.id.qrcode);

        myIP = getIPAddress(true);

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                getClientIpForSignal();
            }
        }).start();*/

        SignalThread signalThread = new SignalThread();
        signalThread.start();

        // QR코드 생성
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(myIP, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            img_qrcode.setImageBitmap(bitmap);
            myDialog.show();
        }catch (Exception e){}
    }

    /* 후방 휴대폰의 IP를 얻기 위한 소켓통신 메서드 */
    public void getClientIpForSignal() {
        try {
            int port = 10000;

            // 서버 소켓 객체 생성
            ServerSocket server = new ServerSocket(port);

            while(true) {
                Socket socket = server.accept();
                InetAddress clientHost = socket.getLocalAddress();
                int clientPort = socket.getPort();
                printServerLog("클라이언트 연결됨 : " + clientHost + " : " + clientPort);

                ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
                Object obj = instream.readObject();
                printServerLog("데이터 받음 : " + obj);

                ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
                outstream.writeObject(obj + " from Server.");
                outstream.flush();
                printServerLog("데이터 보냄");
                printServerLog(obj.toString());
                clientIP = obj.toString();
                myDialog.dismiss();
                isUseBackPhone = true;
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 후방 객체와의 거리를 후방 휴대폰으로부터 획득하기 위한 메서드 */
    public void getBackOjbectDistance() {
        try {
            int port = 10001;

            // 서버 소켓 객체 생성
            ServerSocket server = new ServerSocket(port);

            while(true) {
                Socket socket = server.accept();
                InetAddress clientHost = socket.getLocalAddress();
                int clientPort = socket.getPort();
                printServerLog("클라이언트 연결됨 : " + clientHost + " : " + clientPort);

                ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
                Object obj = instream.readObject();
                printServerLog("데이터 받음 : " + obj);

                Log.d(TAG, obj.toString());

                sensorValue = obj.toString();
                // customView.senValue1 = Integer.parseInt(sensorValue);

                String[] senValueArr = sensorValue.split(" : ");
                customView.senValue1 = Integer.parseInt(senValueArr[0]);
                customView.senValue2 = Integer.parseInt(senValueArr[1]);
                customView.senValue3 = Integer.parseInt(senValueArr[2]);

                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 후방 휴대폰에 방향지시등 정보 전송용 소켓통신 메서드 */
    public void sendSignaltoBackPhone(String data) {
        try {
            int port = 10002;

            Socket socket = new Socket(clientIP, port);
            Log.d(TAG, "방향지시등 전송용 소켓 연결");

            // 데이터 전송
            ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
            outstream.writeObject(data);
            outstream.flush();
            Log.d(TAG, data + " 방향지시등 전송 완료");

            socket.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void printServerLog(final String data) {
        Log.d("MainActivity", data);
    }

    /* 내 라이딩 코스 획득 메서드 */
    private RetrofitAPI retrofitAPI;
    private void setMyRecord(String title, double distance, int time, String startPoint, String endPoint,
                             double avgSpeed, double maxSpeed, Integer routeID, JSONArray records) {
        retrofitAPI = RetrofitClient.getApiService();

        Call<JSONObject> call = retrofitAPI.setMyRecord(Token.getToken(), title, distance, time,
                startPoint, endPoint, avgSpeed, maxSpeed, routeID, records);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(RidingActivity.this, "라이딩 경로 저장", Toast.LENGTH_SHORT).show();

                    if(isUseBackPhone) {
                        thread = new Thread() {
                            public void run() {
                                sendSignaltoBackPhone("E");
                            }
                        };
                        thread.start();
                    }

                    timeThread.interrupt();

                    Intent intent = new Intent(RidingActivity.this, BottomNavigationActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(RidingActivity.this, "라이딩 경로 저장 실패", Toast.LENGTH_SHORT).show();
                    try {
                        Log.d(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Toast.makeText(RidingActivity.this, "라이딩 경로 저장 실패2", Toast.LENGTH_SHORT).show();
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private double currentTemp = 0, currentWindSpeed = 0, currentWindDeg = 0;
    private WeatherAPI weatherAPI;
    private void getWeather(double lat, double lng) {
        weatherAPI = RetrofitWeather.getWeatherApiService();

        Call<Weather> call = weatherAPI.getWeather(lat, lng, WeatherApiKey.getWeatherKey(), "metric");
        call.enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                if(response.isSuccessful()) {
                    // Toast.makeText(RidingActivity.this, "날씨 정보 획득", Toast.LENGTH_SHORT).show();
                    currentTemp = response.body().getMain().getTemp();
                    currentWindSpeed = response.body().getWind().getSpeed();
                    currentWindDeg = response.body().getWind().getSpeed();
                } else {
                    // Toast.makeText(RidingActivity.this, "날씨 정보 획득 실패", Toast.LENGTH_SHORT).show();
                    try {
                        Log.d(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                // Toast.makeText(RidingActivity.this, "날씨 정보 획득 실패2", Toast.LENGTH_SHORT).show();
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private String parseDegree(double degree) {
        String[] sectors = new String[]{ "북", "북동", "동", "남동", "남", "남서", "서", "북서" };

        degree += 22.5;

        if (degree < 0)
            degree = 360 - Math.abs(degree) % 360;
        else
            degree = degree % 360;

        int which = (int)(degree / 45);
        return sectors[which];
    }

    private void getDetailCourse(int id) {
        retrofitAPI = RetrofitClient.getApiService();

        Call<CourseDetailResponse> call = retrofitAPI.getDetailCourse(Token.getToken(), id);
        call.enqueue(new Callback<CourseDetailResponse>() {
            @Override
            public void onResponse(Call<CourseDetailResponse> call, Response<CourseDetailResponse> response) {
                if(response.isSuccessful()) {
                    List<RouteMongoValue> routeValues = response.body().getRoutes().getRouteMongoValue();

                    if(routeValues != null)     setRoutes(routeValues);
                } else {
                    Toast.makeText(getApplicationContext(), "경로 상세정보 조회 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CourseDetailResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "경로 상세정보 조회 실패", Toast.LENGTH_SHORT).show();
                Log.d(TAG, t.getMessage());
            }
        });
    }

    PolylineOptions naviPolylineOptions = new PolylineOptions();
    ArrayList<LatLng> arrayPoints = new ArrayList<LatLng>();
    void setRoutes(List<RouteMongoValue> routes) {
        for(RouteMongoValue route : routes) {
            arrayPoints.add(new LatLng(route.getLat(), route.getLng()));
        }

        naviPolylineOptions.color(Color.RED);
        naviPolylineOptions.width(20);
        naviPolylineOptions.addAll(arrayPoints);

        LatLng startPosition = new LatLng(routes.get(0).getLat(), routes.get(0).getLng());

        // map.animateCamera(CameraUpdateFactory.newLatLngZoom(startPosition, 15));
        map.addPolyline(naviPolylineOptions);
    }
}