package com.example.admin.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.model.Subject;
import com.example.admin.speakingenglishiseasy.R;

import java.util.List;

/**
 * Created by admin on 7/24/2017.
 */

public class SubjectAdapter extends  ArrayAdapter<Subject> {

    // Activity context : current activity that is using
    //int resource : item of listview that truyền vào
    private Activity context;
    private  int resource;
    private  List<Subject> objects; //sourse data
    private  ImageView imgImage;
    private TextView txtSubject;
    private  TextView txtNumber;

    public SubjectAdapter(Activity context, int resource, List<Subject> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       //class LayoutInflater : build layout bình thường thành code java mà android có thể dùng được
        LayoutInflater inflater = this.context.getLayoutInflater();
        View item = inflater.inflate(this.resource,null);

        imgImage = (ImageView) item.findViewById(R.id.imgImage);
        txtNumber = (TextView) item.findViewById(R.id.txtNumber);
        txtSubject = (TextView) item.findViewById(R.id.txtSubjects);

        final Subject subject = this.objects.get(position);
        txtNumber.setText(String.valueOf(subject.getNumber()));
        txtSubject.setText(subject.getSubjects());
        imgImage.setImageBitmap(subject.getImage());



        return item;
    }



}
