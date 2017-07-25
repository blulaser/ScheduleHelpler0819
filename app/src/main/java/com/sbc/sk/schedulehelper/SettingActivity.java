package com.sbc.sk.schedulehelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class SettingActivity extends Activity {



    private Button btnShowLocation;
    private Button serviceon;
    private Button serviceoff;

    private TextView txtLat;

    private TextView txtLon;



    // GPSTracker class

    private GPS gps;



    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.setting_layout);


        btnShowLocation = (Button) findViewById(R.id.btn_start);
        serviceon = (Button)findViewById(R.id.Serviceon);
        txtLat = (TextView) findViewById(R.id.Latitude);
        txtLon = (TextView) findViewById(R.id.Longitude);






        serviceon.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0){
                gps = new GPS(SettingActivity.this);
                gps.getLocation();
            }
        });



        // GPS 정보를 보여주기 위한 이벤트 클래스 등록


        btnShowLocation.setOnClickListener(new View.OnClickListener() {



            public void onClick(View arg0) {

                gps = new GPS(SettingActivity.this);
                gps.getLocation();



                if (gps.isGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    txtLat.setText(String.valueOf(latitude));
                    txtLon.setText(String.valueOf(longitude));

                    Toast.makeText(getApplicationContext(),"당신의 위치 - \n위도: " + latitude + "\n경도: " + longitude, Toast.LENGTH_LONG).show();


                    Intent intent = new Intent(
                            getApplicationContext(),//현재제어권자
                            Service2.class); // 이동할 컴포넌트
                    startService(intent); // 서비스 시작

                } else {
                    // GPS 를 사용할수 없으므로
                    gps.showSettingsAlert();
                }




            }


        });






    }


}

