package com.example.admin.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.model.Topic;
import com.example.admin.speakingenglishiseasy.R;

import java.util.List;

import static com.example.admin.speakingenglishiseasy.Subject_Activity.isTopic;

/**
 * Created by admin on 7/25/2017.
 */

public class TopicAdapter extends ArrayAdapter<Topic> {
    Activity context;
    int resource;
    List<Topic> objects;

    TextView txtSTT;
    TextView txtTopic;
    public  static ImageView imgBtnStatus;

    boolean status;

    public TopicAdapter(Activity context, int resource, List<Topic> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View item = inflater.inflate(resource,null);

        txtSTT = (TextView) item.findViewById(R.id.txtSTT);
        txtTopic = (TextView) item.findViewById(R.id.txtTopics);
        imgBtnStatus = (ImageView) item.findViewById(R.id.imgBtnStatus);

        Topic topic = this.objects.get(position);
        txtSTT.setText(String.valueOf(topic.getsTT()));
        txtTopic.setText(topic.getTopics());

        status = topic.isStatus();
        if(status){
            imgBtnStatus.setImageDrawable(this.context.getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
        }else{
            imgBtnStatus.setImageDrawable(this.context.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
        }

       /* if(isTopic == 1 || isTopic == 3){


        }else  if(isTopic == 2){
            imgBtnStatus.setImageDrawable(this.context.getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
        }*/

        return item ;
    }
}
