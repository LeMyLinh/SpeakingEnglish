package com.example.admin.model;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

/**
 * Created by admin on 7/26/2017.
 */

public class Conversation implements Serializable {
    private  int idConversation;
    private int idTopic;
    private  String personA;
    private String personB;



    public Conversation() {
    }

    public Conversation(int idConversation, int idTopic, String personA, String personB) {
        this.idConversation = idConversation;
        this.idTopic = idTopic;
        this.personA = personA;
        this.personB = personB;
    }

    public int getIdConversation() {
        return idConversation;
    }

    public void setIdConversation(int idConversation) {
        this.idConversation = idConversation;
    }

    public int getIdTopic() {
        return idTopic;
    }

    public void setIdTopic(int idTopic) {
        this.idTopic = idTopic;
    }

    public String getPersonA() {
        return personA;
    }

    public void setPersonA(String personA) {
        this.personA = personA;
    }

    public String getPersonB() {
        return personB;
    }

    public void setPersonB(String personB) {
        this.personB = personB;
    }
}
