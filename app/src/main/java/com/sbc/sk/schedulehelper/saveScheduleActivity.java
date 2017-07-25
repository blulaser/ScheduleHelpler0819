package com.sbc.sk.schedulehelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017-07-23.
 */

public class saveScheduleActivity extends Activity {



    private Button btn1;


    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saveschedule);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat hour_now = new SimpleDateFormat("yy/MM/dd HH:mm");
        String hour_s = hour_now.format(date);
        editText1 = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        btn1 = (Button) findViewById(R.id.button);



        editText2.setText("60");
        editText3.setText(hour_s);




        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String et1 = editText1.getText().toString();
                String et2 = editText2.getText().toString();
                String et3 = editText3.getText().toString();
                byte[] b1 = et1.getBytes();



               // Toast.makeText(getApplicationContext(),et3,Toast.LENGTH_LONG).show();


                int year =  Integer.parseInt(et3.substring(0,2));
                int month = Integer.parseInt(et3.substring(3,5));
                int day = Integer.parseInt(et3.substring(6,8));
                int hour = Integer.parseInt(et3.substring(9,11));
                int minute = Integer.parseInt(et3.substring(12,14));

                Toast.makeText(getApplicationContext(),year+" "+month+ " "+ day + " "+hour + " "+ minute,Toast.LENGTH_LONG).show();

               // ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                //byteArrayOutputStream.write(b1,0,b1.length);
               // byteArrayOutputStream.write(year);
                //byte[] a =  byteArrayOutputStream.toByteArray();
                //String a2b = new String(a,0,a.length);



               // Toast.makeText(getApplicationContext(),a2b,Toast.LENGTH_LONG).show();


           }
        });

    }










}
