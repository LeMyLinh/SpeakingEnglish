package com.example.admin.speakingenglishiseasy;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.adapter.TopicAdapter;
import com.example.admin.model.Subject;
import com.example.admin.model.Topic;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.PriorityQueue;


import static com.example.admin.speakingenglishiseasy.Conversation_Activity.mediaPlayer;
import static com.example.admin.speakingenglishiseasy.Conversation_Activity.pause;
import static com.example.admin.speakingenglishiseasy.Subject_Activity.DATABASE_NAME;
import static com.example.admin.speakingenglishiseasy.Subject_Activity.database;
import static com.example.admin.speakingenglishiseasy.Subject_Activity.isTopic;


public class Topic_Activity extends AppCompatActivity {


    public static    ArrayList<Topic> arrTopics;
    private  TopicAdapter topicAdapter;
    private  ListView lvTopics;

    private String subject;
    private  String idSubject;
    //private byte[] imgSubject;
    private ImageView imgImage;


   // private  ArrayList<Integer> arrIdTopic;
    //private  ArrayList<String> arrMp3;

    private TextView txtAcbTopic;
    private ImageButton imgBackTopic ;


    private  int idTopics;
    private  String topics;
    private boolean status;
    private  String linkMp3;
    private  int numberTopic;
    private  String pathMp3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //chỉ cho màn hình xoay bề dọc thôi
        //cách 2 :    android:screenOrientation="portrait" trong <activity>...  />


        if(isTopic == 1){
            Intent intent = getIntent();

            subject =  intent.getStringExtra("Subject");
            idSubject = intent.getStringExtra("idSubject");
            numberTopic = intent.getIntExtra("numberTopic",-1);
            //imgSubject = intent.getByteArrayExtra("image");
        } else if(isTopic == 2){
           // Toast.makeText(Topic_Activity.this, "Topic_Activity", Toast.LENGTH_SHORT).show();
        }
        else if(isTopic == 3){
             //Toast.makeText(Topic_Activity.this, "Topic_Activity", Toast.LENGTH_SHORT).show();
        }

        addControl();
        querySeLectAll();
        addActionBar();
        addEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
      //if(like ){
         querySeLectAll();
        //Toast.makeText(Topic_Activity.this, "onResume", Toast.LENGTH_SHORT).show();
      //}


    }

    @Override
    protected void onPause() {
        super.onPause();
        //Toast.makeText(Topic_Activity.this, "onPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
          if(requestCode == 99 && resultCode == 33){
             // querySeLectAll();
          }
    }
    private void addControl() {
        lvTopics = (ListView) findViewById(R.id.lvTopics);
        arrTopics = new ArrayList<>();
        topicAdapter =new TopicAdapter(Topic_Activity.this,R.layout.item_topic,arrTopics);
        lvTopics.setAdapter(topicAdapter);

        //arrIdTopic = new ArrayList<>();
        //arrMp3 = new ArrayList<>();

    }
    private void querySeLectAll() {
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = null;
        if(isTopic == 1) {
            cursor = database.rawQuery("select * from Topic where idSubject=?", new String[]{idSubject});
        }else if(isTopic == 2){

            cursor = database.rawQuery("select * from Topic where Status=?", new String[]{"1"});
        }
        else if (isTopic == 3){
            cursor  = database.rawQuery("select * from Topic where PathMp3 <> ?", new String[]{"null"});

        }
        arrTopics.clear();
        int i = 1;
        while(cursor.moveToNext()){
            idTopics = cursor.getInt(0);
            idSubject = cursor.getString(1);
            topics = cursor.getString(2);
            status =  (cursor.getShort(3) != 0);
            linkMp3 = cursor.getString(4);
            pathMp3 = cursor.getString(5);
            Topic topic = null;
            //if(isTopic == 1){
                topic= new Topic(i,idTopics,idSubject,topics,status,linkMp3,pathMp3);
/*
        }else if(isTopic == 2){
            topic= new Topic(i,idTopics,topics,status,linkMp3);
        }else if(isTopic == 3){
            topic = new Topic(i,idTopics,topics,pathMp3);
        }*/

            arrTopics.add(topic);
            // arrIdTopic.add(idTopics);
            // arrMp3.add(linkMp3);
            i++;
        }

        cursor.close();
        topicAdapter.notifyDataSetChanged();

    }
    private void animationClick(View v){
        Animation animation = AnimationUtils.loadAnimation(Topic_Activity.this,R.anim.bounce);
        v.startAnimation(animation);
    }
    private void addActionBar() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater  inflater = LayoutInflater.from(this);
        View item = inflater.inflate(R.layout.item_action_bar_topic,null);

        txtAcbTopic = (TextView) item.findViewById(R.id.txtAcbTopic);
        imgBackTopic = (ImageButton) item.findViewById(R.id.imgBackTopic);
       // imgImage = (ImageView) item.findViewById(R.id.imgSubject);

        if(isTopic == 1){
            txtAcbTopic.setText(subject);
            // imgImage.setImageBitmap(BitmapFactory.decodeByteArray(imgSubject,0,imgSubject.length));
        }else if(isTopic == 2){
            txtAcbTopic.setText("LIKE");
        }else if(isTopic == 3){
            txtAcbTopic.setText("DOWNLOAD");
        }
       
        actionBar.setCustomView(item);
        actionBar.setDisplayShowCustomEnabled(true);


    }
    private void addEvent() {
        lvTopics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent intent = new Intent(Topic_Activity.this, Conversation_Activity.class);
                //intent.putExtra("idTopic",arrTopics.get(position).getIdTopic());
                //intent.putExtra("mp3",arrTopics.get(position).getLinkMp3());
                intent.putExtra("topic",arrTopics.get(position));
                intent.putExtra("position",position);//vị trí trong listview
                //intent.putExtra("numberTopics",numberTopic);
                //intent.putExtra("status",arrTopics.get(position));

                //Toast.makeText(Topic_Activity.this, String.valueOf(arrIdTopic.get(position)), Toast.LENGTH_SHORT).show();
                if(pause){
                    mediaPlayer.stop();
                    pause = false;
                }
                startActivityForResult(intent,99);


            }
        });
        imgBackTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animationClick(v);
                setResult(53);
                finish();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_topic,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId(); //this is item user click into
        if(id == R.id.mnuTopic){
            Toast.makeText(this, "Translate", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


}
