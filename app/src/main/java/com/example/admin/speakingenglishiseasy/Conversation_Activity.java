package com.example.admin.speakingenglishiseasy;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.adapter.ConversationAdapter;
import com.example.admin.model.Conversation;
import com.example.admin.model.Topic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


import static com.example.admin.speakingenglishiseasy.Subject_Activity.DATABASE_NAME;
import static com.example.admin.speakingenglishiseasy.Subject_Activity.database;
import static com.example.admin.speakingenglishiseasy.Subject_Activity.isTopic;
import static com.example.admin.speakingenglishiseasy.Topic_Activity.arrTopics;


public class Conversation_Activity extends AppCompatActivity {

    public  static   int IdTopic;
    public  static  boolean like = false;
    private  String mp3;

    private ListView lvConversation;
    private ConversationAdapter  conversationAdapter;
    private  ArrayList<Conversation> arrConversation;

    private int idConVersation, idTopic,position;
    private  String personA, personB;
    private Topic topic = null;
    private TextView txtTimeBegin,txtTimeEnd;
    private SeekBar seekBar = null;
    private ImageButton imgDownload,imgNextLeft,imgPause,imgNextRight,imgRelease,imgTranslate;
    public  static   MediaPlayer mediaPlayer = null;
    public  static  boolean pause = false;
    private  Handler handler = new Handler();
    private  TextView txtAcbConversation;
    private  ImageButton imgBackConversation,imgLike;
    private  int timeBegin = 0;
    private  boolean replaceColorVolume = false;
    private   ActionBar actionBar;
    private ProgressDialog dialog;
    private String nameOfFile = null;
    private boolean returnConectityLast = false;//Kiểm chuyển từ states không kết nối sang kết nối (true) or ngược lại(false)
    private ProgressDialog dialog1;
    private String pathMp3 ;
    private boolean isEventPause = false;
    private boolean isPreviousActivity = false;
    private MyTask myTask = null;
    private boolean isEventNextLeft = false;
    private boolean isEventNextRight = false;
    private  Handler handlerReplease = null;
    private  boolean isReplease = false;

    //document : http://laptrinhandroid.edu.vn/huong-dan-su-dung-thread-va-handler-trong-android/
    //  http://www.androidhive.info/2012/03/android-building-audio-player-tutorial/#27
    //https://thangcoder.com/lap-trinh-android/hoc-lap-trinh-android-can-ban/huong-dan-luu-tru-va-clone-code-tren-github-tren-android-studio
    private  class  MyTask extends AsyncTask<Void,Void,Integer>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog1= new ProgressDialog(Conversation_Activity.this);
            dialog1.setCanceledOnTouchOutside(false);
            dialog1.setMessage("Loading....");
            dialog1.show();
        }
        @Override
        protected Integer doInBackground(Void... params) {

            if(!isEventPause || isEventNextLeft || isEventNextRight
                    || returnConectityLast ) {
                setDataSourseMp3();
                mediaPlayer.start();
            }
            return mediaPlayer.getDuration();
        }
        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            dialog1.dismiss();
            if(isEventNextLeft){
                listeningMp3(integer);
                isEventNextLeft = false;
            }
            else if(isEventNextRight){
                listeningMp3(integer);
                isEventNextRight = false;
            }
            else {
                    isEventPause = false;
                    examplayMP3(integer);
            }
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(examConnectInternet()){
                        if (!replaceColorVolume && !mp.isPlaying()){ // next mp3
                            isEventNextRight = true; //giá trị biến này chỉ tồn tại trong Asytask
                            nextMp3((short) 1);
                        }
                        if(replaceColorVolume && !mp.isPlaying()){ //lặp lại
                            isEventNextRight = true; // isEventNextLeft = true cũng được
                            setDataSourseMp3InAsytask();
                        }
                    }

                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED); //khóa xoay lại
        //cách 2 :    android:screenOrientation="locked" trong <activity>...  />

        getDataFromActivityOther();
        addControl();
        querySelectDatabase();
        if(!examConnectInternet() && isTopic == 3){
            mp3 = pathMp3;
        }

        setDataSourseMp3InAsytask();

        addActionBar();
        addEvent();

        if( isTopic==1 && topic.isStatus()) {
            like = true;
            imgLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
        }
        else if(isTopic == 2){

        }
        else if(isTopic == 3){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isConected = examConnectInternet();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(isPreviousActivity){
            mediaPlayer.stop();
            myTask.cancel(true);
        }
    }

    private boolean examConnectInternet() {

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(!isConnected){
            if(isTopic == 1 || isTopic == 2){
                messageDialog();
            }
        }else{
            dialog.dismiss();
        }
        return isConnected;
    }
    private void messageDialog(){
        dialog.setMessage("Không có kết nối internet");
        dialog.show();
    }

    private  void examplayMP3(int timeEnd){

        if(examConnectInternet() ){
            playMp3HaveInternetOrOffline(timeEnd);
        }else {
            if(isTopic == 1 || isTopic == 2){
                returnConectityLast = true;
                imgPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_black_24dp));
            }else if(isTopic == 3){
                playMp3HaveInternetOrOffline(timeEnd);
            }
        }
    }

    private void playMp3HaveInternetOrOffline(int timeEnd) {
        if(!pause ){
            pause = true;
            //khi chuyển từ states không kết nối sang kết nối thì setDataSoures()
            if(returnConectityLast){
                if(mediaPlayer.getCurrentPosition()<1){

                    mediaPlayer.stop();
                    setDataSourseMp3InAsytask();
                }
            }
            if(!mediaPlayer.isPlaying() && !returnConectityLast){//chạy tiếp khi pause
                mediaPlayer.start();
            }
            listeningMp3(timeEnd);
            returnConectityLast = false;

        }
        else {
            pause = false;
            imgPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_black_24dp));
            mediaPlayer.pause();
        }
    }

    private void setDataSourseMp3InAsytask() {
        myTask = new MyTask();
        myTask.execute();
    }

    private void getDataFromActivityOther(){
        Intent intent = getIntent();
        topic = (Topic) intent.getSerializableExtra("topic");
        mp3 = topic.getLinkMp3();
        IdTopic = topic.getIdTopic();
        position = intent.getIntExtra("position",-1);
        pathMp3 = topic.getPathMp3();

    }

    private void addActionBar() {

        actionBar= getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflater = getLayoutInflater();
        View item = inflater.inflate(R.layout.item_action_bar_conversation, null);
        txtAcbConversation = (TextView) item.findViewById(R.id.txtAcbConversation);
        imgBackConversation = (ImageButton) item.findViewById(R.id.imgBackConversation);
        imgLike = (ImageButton) item.findViewById(R.id.imgLike);

        txtAcbConversation.setText(topic.getTopics());


        actionBar.setCustomView(item);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    private void addControl() {
        lvConversation = (ListView) findViewById(R.id.lvConversation);
        arrConversation = new ArrayList<>();
        conversationAdapter = new ConversationAdapter(Conversation_Activity.this,R.layout.item_conversation,arrConversation);
        lvConversation.setAdapter(conversationAdapter);


        txtTimeBegin = (TextView) findViewById(R.id.txtTimeBegin);
        txtTimeEnd = (TextView) findViewById(R.id.txtTimeEnd);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        imgDownload = (ImageButton) findViewById(R.id.imgDownload);
        imgNextLeft = (ImageButton) findViewById(R.id.imgNextLeft);
        imgNextRight = (ImageButton) findViewById(R.id.imgNextRight);
        imgPause = (ImageButton) findViewById(R.id.imgPause);
        imgRelease = (ImageButton) findViewById(R.id.imgRelease);

        imgNextRight.setColorFilter(Color.parseColor("#FF4F5253"));
        imgNextRight.setMaxHeight(40);
        imgNextRight.setMaxWidth(60);
        imgNextLeft.setColorFilter(Color.parseColor("#FF4F5253"));
        imgNextLeft.setMaxHeight(40);
        imgNextLeft.setMaxWidth(60);

        dialog = new ProgressDialog(Conversation_Activity.this);

    }

    private void querySelectDatabase() {
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor =  database.rawQuery("select * from Conversation where idTopic=?",
                new String[] {String.valueOf(IdTopic)});
        arrConversation.clear();
        while (cursor.moveToNext()){
            idConVersation = cursor.getInt(0);
            idTopic = cursor.getInt(1);
            personA = cursor.getString(2);
            personB = cursor.getString(3);

            Conversation conversation = new Conversation(idConVersation,idTopic,personA,personB);
            arrConversation.add(conversation);

        }
    }
    private void startColorAnimation(View v) {

        int colorStart = v.getSolidColor();
        int colorEnd = 0xFFFF0000;
        ValueAnimator valueAnimator = ObjectAnimator.ofInt(v,"ColorFilter",colorStart,colorEnd);
        valueAnimator.setDuration(500);
        valueAnimator.setEvaluator(new ArgbEvaluator());
        valueAnimator.setRepeatCount(1);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.start();

    }
    private void animationClick(View v){
        Animation animation = AnimationUtils.loadAnimation(Conversation_Activity.this,R.anim.bounce);
        v.startAnimation(animation);
    }
    private void addEvent() {

        imgPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startColorAnimation(v);
                animationClick(v);
                isEventPause = true;
                setDataSourseMp3InAsytask();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser && mediaPlayer != null){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        imgRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startColorAnimation(v);
                animationClick(v);
                examResetVolume();

            }
        });
        imgBackConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startColorAnimation(v);
                animationClick(v);
                isPreviousActivity = true;
                setResult(33); //đánh dấu kết quả trả về
                finish(); //phải đóng màn hình này lại để màn hình
                // Topic_activity trở thành Foreground Lifetime (vòng đời sau onResume ) Vì màn hình
                // Topic_activiry chỉ nhận trong Foregroind LifeTime
            }
        });

        imgNextLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startColorAnimation(v);
                animationClick(v);
                isEventNextLeft = true;
                nextMp3((short)2);

            }
        });
        imgNextRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startColorAnimation(v);
                animationClick(v);
                isEventNextRight = true;
                nextMp3((short)1);

            }
        });
        imgDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startColorAnimation(v);
                animationClick(v);
                if(examConnectInternet()){
                    downloadMp3();
                }
            }
        });
        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startColorAnimation(v);
                animationClick(v);
                setOnLikeConveraton();
            }
        });

    }

    private void downloadMp3() {
        //phải sửa lại cái down loadmanger
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mp3));
        request.setTitle("File download");

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        nameOfFile = URLUtil.guessFileName(mp3,null,
                MimeTypeMap.getFileExtensionFromUrl(mp3));

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,nameOfFile);

        DownloadManager downloadManager = (DownloadManager) Conversation_Activity.this.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);


        examDowloadCompelete();

    }

    public void examDowloadCompelete() {
        BroadcastReceiver  receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long receivedID = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
                DownloadManager mgr = (DownloadManager)
                        context.getSystemService(Context.DOWNLOAD_SERVICE);

                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(receivedID);
                Cursor cur = mgr.query(query);
                int index = cur.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if(cur.moveToFirst()) {
                    if(cur.getInt(index) == DownloadManager.STATUS_SUCCESSFUL){
                        dialog.dismiss();
                        getPathMp3InSystem();
                    }
                }
                cur.close();
            }


        };

        registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    private void getPathMp3InSystem() {
        //have phones  saved  file mp3 in Audio or in Download
        File files[] = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();
        if(files.length > 0){
            for(int i =0 ;i<files.length ;i++){
                if(files[i].getName().equals("Audio")){
                    File fileMp3[] = files[i].listFiles();
                    if(fileMp3.length > 0){
                        for(int j=0;j<fileMp3.length;j++){
                            String nameFile = URLUtil.guessFileName(fileMp3[j].getAbsolutePath(),null,
                                    MimeTypeMap.getFileExtensionFromUrl(fileMp3[j].getAbsolutePath()));

                            if(nameFile.equals(nameOfFile)){ // đuôi của file

                                updatePathMp3IntoSqlite(fileMp3[j].getAbsolutePath());
                            }
                        }
                    }else{
                        Log.e("MESSAGE","HAVE NOT FILE MP3");
                    }

                    break;

                }
            }
        }


    }

    private void updatePathMp3IntoSqlite(String pathMp3) {
        ContentValues values = new ContentValues(); //chính là dòng
        values.put("PathMp3",pathMp3);
        database.update("Topic",values,"IdTopic=?",new String[]{String.valueOf(IdTopic)});
    }

    private void nextMp3(short nextRightOrLeft) {
        examUnitEnd(nextRightOrLeft); //kiểm tra có phải bài cuối chưa
        changeTextActionBar();
        changeTextConversation();
        changeMp3();
        changeStatusLike();
        changeResetVolume();
    }

    private void examUnitEnd(short nextRightOrLeft) {
        if(nextRightOrLeft == 1 ){ //nextRight

            if(position == arrTopics.size() - 1){
                position = 0;
            }else{
                position++;
            }
        }
        else if(nextRightOrLeft == 2) { //nextLeft

            if(position == 0){
                position = arrTopics.size() - 1;

            }else{
                position --;
            }
        }
        IdTopic = arrTopics.get(position).getIdTopic();

    }
    private void changeTextActionBar() {
        txtAcbConversation.setText(arrTopics.get(position).getTopics());
    }
    private void changeTextConversation() {
        querySelectDatabase();
        conversationAdapter.notifyDataSetChanged();
    }
    private void changeMp3() {
        mediaPlayer.stop();
        if(examConnectInternet()){
            mp3 = arrTopics.get(position).getLinkMp3();
            setDataSourseMp3InAsytask();
        }else{
            if(isTopic == 1 || isTopic == 2){
                seekBar.setProgress(0);
            }else if (isTopic == 3){
                mp3  = arrTopics.get(position).getPathMp3();
                setDataSourseMp3InAsytask();
            }
        }

    }
    private void changeStatusLike() {
        if(arrTopics.get(position).isStatus()){
            imgLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
        }else{
            imgLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
        }
    }
    private void changeResetVolume() {
        if(replaceColorVolume){
            replaceColorVolume = false;
            imgRelease.setImageDrawable(getResources().getDrawable(R.drawable.ic_autorenew_black_24dp));
        }
    }

    private void setOnLikeConveraton() {
        if(!like){
            imgLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
            like = true;
            Toast.makeText(Conversation_Activity.this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_LONG).show();
        }else{
            like = false;
            imgLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
            Toast.makeText(Conversation_Activity.this, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_LONG).show();

        }
        updateDataInTableTopic();
    }
    private void updateDataInTableTopic() {
        ContentValues values  = new ContentValues();
        values.put("Status",like);
        int up = database.update("Topic",values,"IdTopic=?",new String[]{String.valueOf(IdTopic)});

    }

    private void examResetVolume() {
        if(!replaceColorVolume){
            replaceColorVolume = true;
            imgRelease.setImageDrawable(getResources().getDrawable(R.drawable.ic_autorenew_black_red_24dp));

        }
        else{
            replaceColorVolume = false;
            imgRelease.setImageDrawable(getResources().getDrawable(R.drawable.ic_autorenew_black_24dp));
        }

    }

    private void listeningMp3(int timeEnd) {

        runSeekBar(timeEnd);
        timeForSeekbar(timeEnd,txtTimeEnd);

    }

    private void runSeekBar(int timeEnd) {
        seekBar.setMax(timeEnd);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timeBegin = mediaPlayer.getCurrentPosition();
                timeForSeekbar(timeBegin,txtTimeBegin);
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                seekBar.postDelayed(this,100);
                if(!mediaPlayer.isPlaying()){// not run!
                    imgPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_black_24dp));

                }else{
                    imgPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_black_24dp));
                }

            }
        },100);

    }

    private void setDataSourseMp3() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(mp3);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private  void timeForSeekbar(int time,TextView txtTime){
        txtTime.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time)
        ));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_conversation,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id== R.id.mnuConversation){
            Toast.makeText(Conversation_Activity.this, "Translate", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }





}
