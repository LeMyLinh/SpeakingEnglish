package com.example.admin.speakingenglishiseasy;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.admin.adapter.SubjectAdapter;
import com.example.admin.model.Subject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;




public class Subject_Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener   {

    private ArrayList<Subject> arrSubject;
    private SubjectAdapter subjectAdapter;
    private ListView lvSubject;

    public static String DATABASE_NAME ="SpeakingEnglishIsEasy.sqlite"; // tên cơ sở dữ liệu
    String DB_PATH_SUFFIX = "/databases/"; // thư mục lưu file cơ sở dữ liệu
    public static SQLiteDatabase database = null; // class cho phép tương tác database

    public  static  int isTopic = 1; // nếu =  1 : màn hình subject mở màn hình topic
                              //  nếu =  2 : màn hình like mở màn hình topic
                                // nếu = 3 :  activity download  will open activity topic

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
         setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //chỉ cho màn hình xoay bề dọc thôi
        //cách 2 :    android:screenOrientation="portrait" trong <activity>...  />



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //xử lý sao chép CSDL từ asset vào hệ thống mobile mới tương tác được
        xuLySaoChepCSDLTuAssetVaoHeThongMoblie();

        addControl();

        addEvent();

        querySelectDatabase();


    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();

    }

    private void querySelectDatabase() {
          // Bước 1 : mở CSDL
        //lệnh bên dưới ko null
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
         //B2 : có thể dùng query or rawQuery
        Cursor cursor =  database.query("Subject",null,null,null,null,null,null);
       // Cursor cursor = database.rawQuery("SELECT * FROM Subject",null);
        arrSubject.clear(); //xóa cái cũ đi
        //trong khi dg di chuyển tới dòng kế tiếp trong bảng


        while (cursor.moveToNext()){

            String idSubject = cursor.getString(0);
            String subjects = cursor.getString(1);
            byte[] blog = cursor.getBlob(2);
            int number = cursor.getInt(3);

           // cover byte[] to bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(blog,0,blog.length);

           arrSubject.add(new Subject(idSubject,subjects,bitmap,number));
        }
         cursor.close();//đóng kết nối
         subjectAdapter.notifyDataSetChanged(); //cập nhật lại adapter
    }


    private void xuLySaoChepCSDLTuAssetVaoHeThongMoblie() {
        File dbFile = getDatabasePath(DATABASE_NAME);
        if(!dbFile.exists()){ //file chưa tồn tại thì vào sao chép
            try{
                CopyDataBaseFromAsset();
            }catch (Exception e){
                Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
            }
        }
    }

    private void CopyDataBaseFromAsset() {
             try{
                   //đọc asset và open
                 InputStream myInput = getAssets().open(DATABASE_NAME);
                 //đọc từ myInput vào outFileName
                 String outFileName = layDuongDanStore();
                 File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
                 if(!f.exists()) {//kiểm tra đường dẫn có tồn tại không

                     f.mkdir(); //tạo thư mục
                 }

                 //mở đường dẫn ra và nó đang rỗng, copy từ myInput vào myOuput
                 OutputStream myOuput = new FileOutputStream(outFileName);
                 byte[] buffer = new byte[1024];
                 int length;
                 //đọc myInput đưa vào mảng buffer, chừng nào còn > 0 thì đọc tiếp
                 while ((length = myInput.read(buffer)) > 0){
                     //ghi xuống databases trong mobile
                     myOuput.write(buffer,0,length);

                 }
                 //nếu không đóng file thì dung lượng data = 0

                 myOuput.flush();
                 myOuput.close();
                 myInput.close();

             }catch (Exception e){
                 Log.e("ERROR COPY DATABASE",e.toString());
             }
    }

    private String layDuongDanStore(){ //lấy đường dẫn lưu trữ
        //trả về thư mục gốc cài đặt cụ thể trỏ đến thu mục gốc của ta
        //đó là : data/data/com.example.admin/databases/SpeakingEnglishIsEasy.sqlite
        //getApplicationInfo().dataDir : data/data/com.example.admin/
        //DB_PATH_SUFFIX : databases/
        //DATABASE_NAME : SpeakingEnglishIsEasy.sqlite/
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;
    }

    private void addControl() {

        lvSubject = (ListView) findViewById(R.id.lvSubject);
        arrSubject = new ArrayList<>();
        subjectAdapter= new SubjectAdapter(Subject_Activity.this,R.layout.item_subject,arrSubject);
        lvSubject.setAdapter(subjectAdapter);

    }




    private void addEvent() {
             lvSubject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                 @Override
                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                           Intent intent = new Intent(Subject_Activity.this, Topic_Activity.class);
                     intent.putExtra("idSubject",  arrSubject.get(position).getIdSubject());
                      intent.putExtra("Subject",  arrSubject.get(position).getSubjects());
                       intent.putExtra("numberTopic",arrSubject.get(position).getNumber());
                   /*  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                     arrSubject.get(position).getImage().compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                     byte[] b = baos.toByteArray();
                     intent.putExtra("image",b);*/
                     isTopic = 1;
                     startActivityForResult(intent,35);
                 }
             });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        getMenuInflater().inflate(R.menu.subject_, menu);

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
            Toast.makeText(this, "Translate", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId(); //take id that user click into

        if (id == R.id.nav_home) {
            // Handle the home action
        } else if (id == R.id.nav_like) {
            isTopic = 2;
            Intent intent = new Intent(Subject_Activity.this,Topic_Activity.class);
            startActivityForResult(intent,12);

        } else if (id == R.id.nav_download) {
            isTopic = 3;
            Intent intent = new Intent(Subject_Activity.this,Topic_Activity.class);
            startActivityForResult(intent,13);

        } else if (id == R.id.nav_share) {
            shareApp();

        } else if (id == R.id.ic_menu_star) {
            rateApp();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getBaseContext().getPackageName() /*"com.vn.dic.e.v.ui"*/);
        Intent gotoMaket = new Intent(Intent.ACTION_VIEW,uri);
        int flags;
        if (Build.VERSION.SDK_INT >= 21)
        {
            flags = Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        }
        else
        {
            //noinspection deprecation
            flags = Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        gotoMaket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | flags |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(gotoMaket);
        }catch (ActivityNotFoundException e){
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="+getBaseContext().getPackageName())));

        }
    }

    private void shareApp() {

         Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain"); //send by text/plain
        String contentSend = "https://play.google.com/store/apps/details?id=" + getBaseContext().getPackageName();
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,"English listening and speaking");
        shareIntent.putExtra(Intent.EXTRA_TEXT,contentSend);
        startActivity(Intent.createChooser(shareIntent,"Share"));

    }


}
