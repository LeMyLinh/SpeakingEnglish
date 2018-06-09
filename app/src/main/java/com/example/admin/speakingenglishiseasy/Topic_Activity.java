package com.example.admin.speakingenglishiseasy;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

import com.example.admin.adapter.TopicAdapter;
import com.example.admin.model.Topic;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;

import static com.example.admin.speakingenglishiseasy.Conversation_Activity.mediaPlayer;
import static com.example.admin.speakingenglishiseasy.Conversation_Activity.pause;
import static com.example.admin.speakingenglishiseasy.Subject_Activity.DATABASE_NAME;
import static com.example.admin.speakingenglishiseasy.Subject_Activity.database;
import static com.example.admin.speakingenglishiseasy.Subject_Activity.isTopic;


public class Topic_Activity extends AppCompatActivity {

    ShareDialog shareDialog;

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //cách 2 :    android:screenOrientation="portrait" trong <activity>...  />


        if(isTopic == 1){
            Intent intent = getIntent();

            subject =  intent.getStringExtra("Subject");
            idSubject = intent.getStringExtra("idSubject");
            numberTopic = intent.getIntExtra("numberTopic",-1);
        }


        addControl();
        querySeLectAll();
        addActionBar();
        addEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        querySeLectAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void addControl() {
        lvTopics = (ListView) findViewById(R.id.lvTopics);
        arrTopics = new ArrayList<>();
        topicAdapter =new TopicAdapter(Topic_Activity.this,R.layout.item_topic,arrTopics);
        lvTopics.setAdapter(topicAdapter);

        shareDialog = new ShareDialog(this);
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
            cursor  = database.rawQuery("select * from Topic where PathMp3 != ?", new String[]{"null"});
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

            arrTopics.add(new Topic(i,idTopics,idSubject,topics,status,linkMp3,pathMp3));
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

        if(isTopic == 1){
            txtAcbTopic.setText(subject);
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
                intent.putExtra("topic",arrTopics.get(position));
                intent.putExtra("position",position);

                if(pause){
                    mediaPlayer.stop();
                    pause = false;
                }
                startActivity(intent);
            }
        });

        imgBackTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animationClick(v);
                finish();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_subject_drawer,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId(); //take id that user click into

        if (id == R.id.nav_home) {
            Intent intent = new Intent(Topic_Activity.this,Subject_Activity.class);
            startActivity(intent);

        } else if (id == R.id.nav_like) {
            isTopic = 2;
            Intent intent = new Intent(Topic_Activity.this,Topic_Activity.class);
            startActivity(intent);

        } else if (id == R.id.nav_download) {
            isTopic = 3;
            Intent intent = new Intent(Topic_Activity.this,Topic_Activity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            shareApp();

        } else if (id == R.id.nav_rate) {
            rateApp();
        }

        return super.onOptionsItemSelected(item);
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
        String url = "https://play.google.com/store/apps/details?id=" + getBaseContext().getPackageName();
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(url))
                .setQuote("This app is so useful!")
                .setShareHashtag(new ShareHashtag.Builder().setHashtag("#GodEnglishAPP").build())
                .build();

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            shareDialog.show(linkContent);
        }
    }
}
