package com.sbc.sk.schedulehelper;

import android.Manifest;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public DatabaseHelper dbHelper;
    public static SQLiteDatabase db;

    private Messenger mService;
    private boolean mBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 초기 화면을 CalendarFragment로 설정.
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.content_main, new CalendarFragment()).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // sdcard 사용 권한 확인 및 요청
        int permissionCheckResult = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheckResult != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Const.MY_PERMISSION_REQUEST_STORAGE);
        } else {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        openDatabase();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Const.MY_PERMISSION_REQUEST_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 외부데이터 작성 권한을 얻어 데이터베이스 구축
                    openDatabase();
                }
                else {
                    // 외부데이터 작성 권한을 거부당해 데이터베이스 구축 하지 않음
                }
                return;
            }
        }
    }

    private void openDatabase() {
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            //Documents 폴더에 데이터베이스 저장(db파일 접근 가능 / 어플 삭제시 db가 살아있음)
            //super(context, Environment.getExternalStorageDirectory().getPath() + "/Documents/" + DATABASE_NAME+ ".db", null, DATABASE_VERSION);

            //앱 데이타 안에 데이터베이스 저장(db파일 접근 불가 / 어플 삭제시 db도 삭제)
            super(context, Const.DATABASE_NAME, null, Const.DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            //String DROP_SQL = "drop table if exists " + TABLE_NAME;
            //db.execSQL(DROP_SQL);

            String CREATE_SQL = "create table " + Const.TABLE_NAME + "("
                    + " _id integer PRIMARY KEY autoincrement, "
                    + " scid integer, "
                    + " sctitle text, "
                    + " startyear integer, "
                    + " startmonth integer, "
                    + " startdate integer, "
                    + " starthour integer, "
                    + " startminute integer, "
                    + " endyear integer, "
                    + " endmonth integer, "
                    + " enddate integer, "
                    + " endhour integer, "
                    + " endminute integer)";
            db.execSQL(CREATE_SQL);

            String CREATE_MEMO = "create table " + Const.TABLE_MEMO + "("
                    + " _id integer PRIMARY KEY autoincrement, "
                    + " memoid integer, "
                    + " contents text)";
            db.execSQL(CREATE_MEMO);

            String CREATE_LOCATION = "create table " + Const.TABLE_LOCATION + "("
                    + " _id integer PRIMARY KEY autoincrement, "
                    + " locationid integer, "
                    + " location text)";
            db.execSQL(CREATE_LOCATION);
        }

        public void onOpen(SQLiteDatabase db) {

        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public static SQLiteDatabase returnDB() {
        return db;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Snackbar.make(getWindow().getDecorView().getRootView(), "아직 지원되지 않는 기능입니다.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            sendMsg(1,1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager manager = getFragmentManager();

        if (id == R.id.nav_calendar) {
            manager.beginTransaction().replace(R.id.content_main, new CalendarFragment()).commit();
        } else if (id == R.id.nav_memo) {
            manager.beginTransaction().replace(R.id.content_main, new MemoFragment()).commit();
        } else {
            Snackbar.make(getWindow().getDecorView().getRootView(), "아직 지원되지 않는 기능입니다.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        bindService(new Intent(this, MessagingService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBound) {
            //getActivity().unbindService(mConnection);
            unbindService(mConnection);
            mBound = false;
        }
    }

    private void sendMsg(int howManyConversations, int messagesPerConversation) {
        if (mBound) {
            Message msg = Message.obtain(null, 1,
                    howManyConversations, messagesPerConversation);
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                Toast.makeText(this, "Error on sendMsg", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
