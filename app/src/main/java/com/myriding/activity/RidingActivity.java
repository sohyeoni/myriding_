package com.myriding.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
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
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
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
    private TextView tv_recAvgSpeed;
    private TextView tv_currentLocation, tv_currentTemp, tv_currentWind;
    private CustomView customView;

    private BluetoothSPP bt;

    Location lastLocation = null;
    List<Polyline> polylines = new ArrayList<>();
    List<Record> myRecords;
    List<Point> myPoints;

    double totalDistance = 0.0;
    double maxSpeed = 0.0;

    Thread thread;
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

        bt = new BluetoothSPP(this); //initializing
        if(!bt.isBluetoothAvailable()){ // 블루투스 사용 불가
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            @Override
            public void onDataReceived(byte[] data, String message) {

                String[] read_data_arry;
                //수신받은 데이터를 , 기준으로 split 함수로 짤라서 read_data_arry 배열에 저장한다
                read_data_arry = message.split("\\,");
                try {
                    // bt.send("P",true);
                    // 첫번째 데이터가 s이고, 마지막 데이터가 e라면 (데이터 정상 수신)
                    if (read_data_arry[0].equals("s") && read_data_arry[4].equals("e")) {

                        //파싱한 거리값 3개를 각각 변수에 저장
                        customView.senValue1 = Integer.parseInt(read_data_arry[1]);
                        customView.senValue2 = Integer.parseInt(read_data_arry[2]);
                        customView.senValue3 = Integer.parseInt(read_data_arry[3]);

                        // 텍스트뷰에 출력할 String 내용을 작성
                        String read_distance = "";
                        read_distance = customView.senValue1 + " : " + customView.senValue2 + " : " + customView.senValue3 + "";
                        Log.e("bluet",read_distance);

                        message = "";
                    } else {
                        Toast.makeText(RidingActivity.this, "READ: " + message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(RidingActivity.this, "READ: " + message + "\n\nERROR: " + e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결 됐을떄
            @Override
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext(), "Connected to " + name + "\n" + address, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext(), "Connection lost", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        startLocationService();

        // 후방 알림 사용여부 확인 후 연결 작업 실시
        showUseBackPhone();

        if(isUseBackPhone) {
            graphicHandler = new Handler();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
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
        customView = (CustomView) findViewById(R.id.customView);

        tv_currentLocation = (TextView) findViewById(R.id.riding_current_location);
        tv_currentTemp = (TextView) findViewById(R.id.riding_current_weather);
        tv_currentWind = (TextView) findViewById(R.id.riding_current_wind);

        // recognizierInit();
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
                // Log.d(TAG + "Start", startPointAddress);
            } else {
                endPointAddress = currentLocationAddress;
                // Log.d(TAG + "End", endPointAddress);
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

            if(lastLocation != null && isRiding && updateTime % 5 == 0)
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

        /*if(tts!=null){
            tts.stop();
            tts.shutdown();
            tts=null;
        }
        if(mRecognizer!=null){
            mRecognizer.destroy();
            mRecognizer.cancel();
            mRecognizer=null;
        }*/
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

        /*if(isUseBackPhone) {
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "권한 미허용");
                ActivityCompat.requestPermissions(RidingActivity.this,new String[]{Manifest.permission.RECORD_AUDIO},1);
                //권한을 허용하지 않는 경우
            }else{
                //권한을 허용한 경우
                try {
                    Log.d(TAG, "권한 허용");
                    mRecognizer.startListening(SttIntent);
                }catch (SecurityException e){e.printStackTrace();}
            }
        }*/

        clickRestart(view);
    }

    public void clickPause(View view) {
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
                        Log.d(TAG, "START");
                        //sendSignaltoBackPhone("S");
                        bt.send("S", true);
                        Log.d(TAG, "S");
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

    public void clickStop(View view) {
        endDialog();
    }

    public void clickLeftSignal(View view) {
        thread = new Thread(){
            public void run(){
                bt.send("L", true);
                Log.d(TAG, "L");
            }
        };
        thread.start();

    }

    public void clickRightSignal(View view) {
        thread = new Thread(){
            public void run(){
                bt.send("R", true);
                Log.d(TAG, "R");
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
                         //if (speedTemp < 1) {
                            bt.send("P", true);
                            lastSignal = "P";
                            Log.d(TAG, "P");
                        } else if(speedTemp > 1 && lastSignal != "S" && isRiding == true) {
                         //} else if (speedTemp > 1) {
                            bt.send("S", true);
                            lastSignal = "S";
                            Log.d(TAG, "S");
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
        dialog.setMessage("후방의 위험 요소 알림 및 방향지시등 기능을 사용하시겠습니까?")
                .setPositiveButton("사용", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isUseBackPhone = true;

                        if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                            bt.disconnect();
                        }else{
                            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                        }
                        //if(isUseBackPhone) {
                        // connectBackPhone();
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

                Log.d(TAG, "라이딩 일지 저장");
                Log.d(TAG, distanceTemp + ", " + avgSpeedTemp + ", " + maxSpeed);
                Log.d(TAG, (myRecordForJson instanceof JSONArray) ? "true" : "false");

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

    public void onStart() {
        super.onStart();
        // 블루투스가 비활성일떄 작업실행
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            //블루투스가 화성화 된 경우 작업 실행
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
            }
        }
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
                                // sendSignaltoBackPhone("E");
                                bt.send("E",true);
                                lastSignal = "E";
                                Log.d(TAG, "E");
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

/*    private void recognizierInit() {
        //음성인식
        SttIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        SttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getApplicationContext().getPackageName());
        SttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");//한국어 사용
        mRecognizer= SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        mRecognizer.setRecognitionListener(listener);

        //음성출력 생성, 리스너 초기화
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=android.speech.tts.TextToSpeech.ERROR){
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
    }


    Intent SttIntent;
    SpeechRecognizer mRecognizer;
    TextToSpeech tts;
    // 리스너 함수
    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Log.d(TAG, "onReadyForSpeech");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "지금부터 말을 해주세요");
        }

        @Override
        public void onRmsChanged(float v) {
            Log.d(TAG, "onRmsChanged");
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            Log.d(TAG, "onBufferReceived");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech");
        }

        @Override
        public void onError(int i) {
            Log.d(TAG, "천천히 다시 말해 주세요");
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        mRecognizer.startListening(SttIntent);
                    }catch (SecurityException e){
                        e.printStackTrace();
                    }
                }
            },2500);
        }

        @Override
        public void onResults(Bundle results) {
            String key= "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult =results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            Log.d(TAG, rs[0]);
            FuncVoiceOrderCheck(rs[0]);
            mRecognizer.startListening(SttIntent);

        }

        @Override
        public void onPartialResults(Bundle bundle) {
            Log.d(TAG, "onPartialResults");
        }


        public void onEvent(int i, Bundle bundle) {
            Log.d(TAG, "onEvent");
        }
    };

    // 음성 동작 함수
    // 입력된 음성 메세지 확인 후 동작 처리
    private void FuncVoiceOrderCheck(String VoiceMsg){
        if(VoiceMsg.length()<1)return;

        VoiceMsg=VoiceMsg.replace(" ","");//공백제거
        if(VoiceMsg.indexOf("왼쪽")>-1){
            Log.d(TAG, "왼쪽");
            FuncVoiceOut("왼쪽");

            thread = new Thread(){
                public void run(){
                    bt.send("L", true);
                    lastSignal = "L";
                    Log.d(TAG, "L");
                }
            };
            thread.start();
        }else if(VoiceMsg.indexOf("오른쪽")>-1){
            Log.d(TAG, "오른쪽");
            FuncVoiceOut("오른쪽");

            thread = new Thread(){
                public void run(){
                    bt.send("R", true);
                    lastSignal = "R";
                    Log.d(TAG, "R");
                }
            };
            thread.start();
        }else if(VoiceMsg.indexOf("멈춰")>-1){
            Log.d(TAG, "멈춰");
            FuncVoiceOut("멈춰");

            thread = new Thread(){
                public void run(){
                    bt.send("P", true);
                    lastSignal = "P";
                    Log.d(TAG, "P");
                }
            };
            thread.start();
        }
    }

    //음성 메세지 출력용 함수
    private void FuncVoiceOut(String OutMsg){
        if(OutMsg.length()<1)return;

        tts.setPitch(1.0f);//목소리 톤1.0
        tts.setSpeechRate(1.0f);//목소리 속도
        tts.speak(OutMsg, TextToSpeech.QUEUE_FLUSH,null);
    }*/

}