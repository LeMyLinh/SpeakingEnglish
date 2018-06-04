package com.example.admin.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.model.Conversation;
import com.example.admin.speakingenglishiseasy.Conversation_Activity;
import com.example.admin.speakingenglishiseasy.R;

import java.util.List;

/**
 * Created by admin on 7/26/2017.
 */

public class ConversationAdapter extends ArrayAdapter<Conversation> {

    private  Activity context;
    private int resource;
    private  List<Conversation> objects;

    private ImageView imgPersonA, imgPersonB;
    public static TextView txtPersonA, txtPersonB;
    private ImageButton imgListenSentenceA,imgListenSentenceB;


    public ConversationAdapter(Activity context, int resource, List<Conversation> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View item = inflater.inflate(this.resource,null);


        imgPersonA = (ImageView) item.findViewById(R.id.imgPersonA);
        imgPersonB = (ImageView) item.findViewById(R.id.imgPersonB);
        txtPersonA = (TextView) item.findViewById(R.id.txtPersonA);
        txtPersonB = (TextView) item.findViewById(R.id.txtPersonB);
        imgListenSentenceA = (ImageButton) item.findViewById(R.id.imgListenSentenceA);
        imgListenSentenceB = (ImageButton) item.findViewById(R.id.imgListenSentenceB);


        Conversation conversation = this.objects.get(position);
        txtPersonA.setText(conversation.getPersonA());
        txtPersonB.setText(conversation.getPersonB());

        //addEvent();
        return item;
    }

    private void addEvent() {
        imgListenSentenceA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "volumeA", Toast.LENGTH_SHORT).show();
            }
        });
        imgListenSentenceB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "volumeB", Toast.LENGTH_SHORT).show();

            }
        });
    }


}
