package com.example.admin.model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by admin on 7/29/2017.
 */

public class Subject implements Serializable {
    private String idSubject;
    private String subjects;
    private Bitmap image;
    private int number;

    public Subject() {
    }

    public Subject(String idSubject, String subjects, Bitmap image, int number) {
        this.idSubject = idSubject;
        this.subjects = subjects;
        this.image = image;
        this.number = number;
    }

    public String getIdSubject() {
        return idSubject;
    }

    public void setIdSubject(String idSubject) {
        this.idSubject = idSubject;
    }

    public String getSubjects() {
        return subjects;
    }

    public void setSubjects(String subjects) {
        this.subjects = subjects;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
