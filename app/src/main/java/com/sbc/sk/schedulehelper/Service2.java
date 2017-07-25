package com.sbc.sk.schedulehelper;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Service2 extends Service {
    Location location;
    protected LocationManager locationManager;

    private GPS gps2;
    private boolean isStop;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private  Context mContext;
    double lat1;
    double lon1;
    Location locationA = new Location("point A");
    double distance;
    Location locationB = new Location("point B");

    // 최소 GPS 정보 업데이트 시간 밀리세컨이므로 1분

    private static final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1;

    @Override
    public IBinder onBind(Intent intent) {
        // Service 객체와 (화면단 Activity 사이에서)
        // 통신(데이터를 주고받을) 때 사용하는 메서드
        // 데이터를 전달할 필요가 없으면 return null;
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        isStop=false;
        gps2 = new GPS(this);

        mContext = this;
        Log.d("test", "서비스의 onCreate");
        Thread counter = new Thread(new Counter()); counter.start();
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);







    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스가 호출될 때마다 실행
        Log.d("test", "서비스의 onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }

    private class  Counter implements Runnable {

        private int count;
        private Handler handler = new Handler();

        //노티피케이션 설정

        Intent ii2= new Intent(getApplicationContext(),saveSchedule.class);

        PendingIntent pi = PendingIntent.getService(getApplicationContext(),0,ii2,PendingIntent.FLAG_UPDATE_CURRENT);



        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("aa")
                        .setContentText("bb")
                        .addAction(R.drawable.ic_launcher,"ss",pi)
                        .setContentIntent(pi);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        byte [] buffer = new byte[6];
        byte [] buffer2 = new byte[2];
        byte [] buffer3 = new byte[2];



        @Override public void run() { for (count = 0; count < 5; count++) { // STOP 버튼을 눌렀다면 종료한다.


        if (isStop) { break; } /** * Thread 안에서는 UI와 관련된 Toast를 쓸 수 없습니다. * 따라서, Handler를 통해 이용할 수 있도록 만들어줍니다. */
        handler.post(new Runnable() { @Override public void run() {
            // Toast로 Count 띄우기


                if(count==0) {
                    lat1 = gps2.lat;
                    lon1 = gps2.lon;
                    locationA.setLatitude(lat1);
                    locationA.setLongitude(lon1);
                    try {
                        pi.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }



            locationB.setLatitude(gps2.lat);
            locationB.setLongitude(gps2.lon);

            distance = locationA.distanceTo(locationB);
                if(distance<100) {
                  //  Toast.makeText(getApplicationContext(), distance + "m," + gps2.lat + "," + gps2.lon + " "+lat1+","+lon1, Toast.LENGTH_SHORT).show();
                }
                else
                {
                   // Toast.makeText(getApplicationContext(), "#"+distance + "m," + gps2.lat + "," + gps2.lon + " "+lat1+","+lon1, Toast.LENGTH_SHORT).show();
                   isStop=true;
                }
                if(count==1)
                {
                    InputStream is = getApplicationContext().getResources().openRawResource(R.raw.schedule);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    String dr;
                    int dr_i=0;

                    try{
                        int a =is.read(buffer);
                        String ymd = new String(buffer,0,a);
                        int ymd_i = Integer.parseInt(ymd);
                        is.read();

                        int b = is.read(buffer2);
                        String hour = new String(buffer2,0,b);
                        int hour_i = Integer.parseInt(hour);
                        int c = is.read(buffer3);
                        String minute = new String(buffer3,0,c);
                        int minute_i = Integer.parseInt(minute);
                        is.read();

                        int d;
                        try{
                            d = is.read();
                        while (d != -1) {
                            byteArrayOutputStream.write(d);
                            d = is.read();
                        }
                            dr = new String(byteArrayOutputStream.toByteArray(),"MS949");
                            dr_i = Integer.parseInt(dr);
                            is.close();
                        }catch(IOException e)
                            {
                                e.printStackTrace();
                            }

                        Toast.makeText(getApplicationContext(),ymd_i+" "+hour_i+" "+dr_i,Toast.LENGTH_SHORT).show();
                        long now =System.currentTimeMillis();
                        Date date =new Date(now);
                        SimpleDateFormat ymd_now = new SimpleDateFormat("yyMMdd");
                        String ymd_s =ymd_now.format(date);
                        SimpleDateFormat hour_now = new SimpleDateFormat("HH");
                        String hour_s = hour_now.format(date);
                        SimpleDateFormat minute_now = new SimpleDateFormat("mm");
                        String minute_s = minute_now.format(date);


                        if(ymd_i==Integer.parseInt(ymd_s) && Math.abs(hour_i*60+minute_i-Integer.parseInt(hour_s)*60-Integer.parseInt(minute_s))<=dr_i)
                        {
                            Toast.makeText(getApplicationContext(),"기존에 있는 스케쥴의 날짜 시간 스케쥴이 지속되는시간은(분)"+ymd_i+" "+hour_i+" "+minute_i+" "+dr_i+"이고 현재날짜와 시간은 "+ymd_s+" "+hour_s+" "+minute_s+"이므로 스케쥴이 겹칩니다",Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"기존에 있는 스케쥴의 날짜 시간 스케쥴이 지속되는시간은(분)"+ymd_i+" "+hour_i+" "+minute_i+" "+dr_i+"이고 현재날짜와 시간은 "+ymd_s+" "+hour_s+" "+minute_s+"이므로 스케쥴이 안겹칩니다",Toast.LENGTH_SHORT).show();

                            mNotificationManager.notify(0,mBuilder.build());

                        }
                    }catch (Exception e){e.printStackTrace();}





                   //TODO 저장할건지 호출하는거 넣는위치
                }

            // Log로 Count 찍어보기
            Log.d("COUNT,", count + ""); } });
        // Sleep을 통해 1초씩 쉬도록 한다.
        try { Thread.sleep(1000*5); } catch (InterruptedException e) { e.printStackTrace(); } } handler.post(new Runnable() { @Override public void run() { Toast.makeText(getApplicationContext(), "서비스가 종료되었습니다.", Toast.LENGTH_SHORT).show(); } }); } }




    @Override
    public void onDestroy() {


        Log.d("test", "서비스의 onDestroy");
        super.onDestroy();
        isStop = true;
        // 서비스가 종료될 때 실행
    }










}
