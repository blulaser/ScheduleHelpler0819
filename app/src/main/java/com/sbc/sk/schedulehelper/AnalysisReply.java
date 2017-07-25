package com.sbc.sk.schedulehelper;

/*
0번기능 : 메모 등록
1번기능 : 스케줄 등록
2번기능 : 스케줄 등록 및 카톡 공유기능
*/

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.IntDef;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AnalysisReply extends Service {
    public DatabaseHelper dbHelper;
    public SQLiteDatabase db;

    public CharSequence command;
    public int fn_number;
    public CharSequence sc_title;
    public int sc_year;
    public int sc_month;
    public int sc_day;
    public int sc_hour;
    public int sc_min;
    public CharSequence memo;

    public AnalysisReply() {
        // This should be NULL
    }

    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CharSequence reply;
        int y;
        reply = intent.getCharSequenceExtra("reply");
        y =Analysis_command(reply);
        if(y==2){
            Intent i = new Intent(getApplicationContext(), ShareActivity.class);

            PendingIntent p = PendingIntent.getActivity(getApplicationContext(), 0, i, 0);
            try {

                p.send();

            } catch (PendingIntent.CanceledException e) {

                e.printStackTrace();

            }
        }
        stopSelf(startId);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int Analysis_command(CharSequence input) {
        dbHelper = new DatabaseHelper(getApplicationContext(), Const.DATABASE_NAME, null, Const.DATABASE_VERSION);
        db = dbHelper.getWritableDatabase();

        //db = MainActivity.returnDB();

        String input_s = input.toString();
        char fn_character = input_s.charAt(0);
        if (fn_character == '0'
                | fn_character == '1'
                | fn_character == '2'
                | fn_character == '3'
                | fn_character == '4'
                | fn_character == '5'
                | fn_character == '6'
                | fn_character == '7'
                | fn_character == '8'
                | fn_character == '9') {
            fn_number = 1;

            String sc_date, sc_time, sc_title_s;
            String[] s_array;
            s_array = input_s.split("\\.");

            sc_date = s_array[0];
            sc_time = s_array[1];
            sc_title_s = s_array[2];

            sc_year = Integer.parseInt(sc_date.substring(0,2));
            sc_month = Integer.parseInt(sc_date.substring(2,4));
            sc_day = Integer.parseInt(sc_date.substring(4,6));

            sc_hour = Integer.parseInt(sc_time.substring(0,2));
            sc_min = Integer.parseInt(sc_time.substring(2,4));

            sc_title = sc_title_s;

            memo = null;

            insertRecord((String) sc_title, (sc_year+2000), sc_month, sc_day, sc_hour, sc_min, (sc_year+2000), sc_month, sc_day, (sc_hour+1), sc_min);
            return 1;
        }

        else if (fn_character == '#') {
            fn_number = 2;

            String sc_date, sc_time, sc_title_s;
            String[] s_array;
            s_array = input_s.split("\\.");

            sc_date = s_array[1];
            sc_time = s_array[2];
            sc_title_s = s_array[3];

            sc_year = Integer.parseInt(sc_date.substring(0,2));
            sc_month = Integer.parseInt(sc_date.substring(2,4));
            sc_day = Integer.parseInt(sc_date.substring(4,6));

            sc_hour = Integer.parseInt(sc_time.substring(0,2));
            sc_min = Integer.parseInt(sc_time.substring(2,4));

            sc_title = sc_title_s;

            memo = null;

            insertRecord((String) sc_title, (sc_year+2000), sc_month, sc_day, sc_hour, sc_min, (sc_year+2000), sc_month, sc_day, (sc_hour+1), sc_min);

            sendRecord((String) sc_title, (sc_year+2000), sc_month, sc_day, sc_hour, sc_min, (sc_year+2000), sc_month, sc_day, (sc_hour+1), sc_min);
            return 2;
        }
        else if(fn_character == '%'){
            Intent i = new Intent(getApplicationContext(), SettingActivity.class);

            PendingIntent p = PendingIntent.getActivity(getApplicationContext(), 0, i, 0);

            try {

                p.send();

            } catch (PendingIntent.CanceledException e) {

                e.printStackTrace();

            }
            return 4;
        }

        else {
            fn_number = 0;

            sc_year = 0;
            sc_month = 0;
            sc_day = 0;

            sc_hour = 0;
            sc_min = 0;

            sc_title = null;

            memo = input;

            insertMemo((String) memo);
            return 0;
        }
    }

    public String getAllMessages() {
        return "----- AnalysisReply -----\n"
                + "fn_number : " + fn_number + "\n\n"
                + "sc_year : " + sc_year + "\n"
                + "sc_month : " + sc_month + "\n"
                + "sc_day : " + sc_day + "\n\n"
                + "sc_hour : " + sc_hour + "\n"
                + "sc_min : " + sc_min + "\n\n"
                + "sc_title : " + sc_title + "\n"
                + "memo : " + memo + "\n";
    }

    public void insertRecord(
            String sctitle,
            int startyear,
            int startmonth,
            int startdate,
            int starthour,
            int startminute,
            int endyear,
            int endmonth,
            int enddate,
            int endhour,
            int endminute
    ) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        int sc_id = pref.getInt("sc_id", 0);
        int sc_account = pref.getInt("sc_account", 0);

        String INSERT_SQL = "insert into " + Const.TABLE_NAME + "("
                + "scid, "
                + "sctitle, "
                + "startyear, "
                + "startmonth, "
                + "startdate, "
                + "starthour, "
                + "startminute, "
                + "endyear, "
                + "endmonth, "
                + "enddate, "
                + "endhour, "
                + "endminute"
                + ") "
                + "values ("
                + sc_id + ", "
                + "'" + sctitle + "', "
                + startyear + ", "
                + startmonth + ", "
                + startdate + ", "
                + starthour + ", "
                + startminute + ", "
                + endyear + ", "
                + endmonth + ", "
                + enddate + ", "
                + endhour + ", "
                + endminute + ");";
        db.execSQL(INSERT_SQL);

        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("sc_id", sc_id+1);
        editor.putInt("sc_account", sc_account+1);
        editor.commit();
    }

    public void insertMemo(String contents) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        int memo_id = pref.getInt("memo_id", 0);
        int memo_account = pref.getInt("memo_account", 0);

        String INSERT_MEMO_SQL = "insert into " + Const.TABLE_MEMO + "("
                + "memoid, "
                + "contents) "
                + "values ("
                + memo_id + ", "
                + "'" + contents + "');";
        db.execSQL(INSERT_MEMO_SQL);

        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("memo_id", memo_id+1);
        editor.putInt("memo_account", memo_account+1);
        editor.commit();
    }

    public void sendRecord(
            String sctitle,
            int startyear,
            int startmonth,
            int startdate,
            int starthour,
            int startminute,
            int endyear,
            int endmonth,
            int enddate,
            int endhour,
            int endminute
    ) {
        String contents = "BEGIN:VCALENDAR\n" +
                "PRODID:-//Daum Calendar Android//iCal4j 1.0//EN\n" +
                "VERSION:2.0\n" +
                "CALSCALE:GREGORIAN\n" +
                "BEGIN:VEVENT\n" +
                "DTSTAMP:20170608T124027Z\n" +
                "SUMMARY:" + sctitle + "\n" +
                "DTSTART;TZID=Asia/Seoul:" +
                String.format("%02d", startyear) + String.format("%02d", startmonth) + String.format("%02d", startdate) +
                "T" +
                String.format("%02d", starthour) + String.format("%02d", startminute) + "00" +
                "\n" +
                "DTEND;TZID=Asia/Seoul:" +
                String.format("%02d", endyear) + String.format("%02d", endmonth) + String.format("%02d", enddate) +
                "T" +
                String.format("%02d", endhour) + String.format("%02d", endminute) + "00" +
                "\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR\n";
        //MakeFileActivity m1 = new MakeFileActivity();
        makefile(contents);
    }

    public void makefile(String s) {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/Documents/" + "schedule" + ".ics");
            fout.write(s.getBytes());
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
