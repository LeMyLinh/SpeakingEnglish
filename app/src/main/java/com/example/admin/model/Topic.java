package com.example.admin.model;

import java.io.Serializable;

/**
 * Created by admin on 7/25/2017.
 */

public class Topic implements Serializable {
    private int idTopic;
    private String idSubject;
    private String topics;
    private boolean status;
    private String linkMp3;
    private  int sTT;

    public String getPathMp3() {
        return pathMp3;
    }

    public void setPathMp3(String pathMp3) {
        this.pathMp3 = pathMp3;
    }

    private String pathMp3;


    public Topic() {
    }

    public Topic(int sTT,int idTopic, String idSubject, String topics, boolean status, String linkMp3,String PathMp3) {
            this.idTopic = idTopic;
            this.idSubject = idSubject;
            this.topics = topics;
            this.status = status;
            this.linkMp3 = linkMp3;
            this.sTT = sTT;
            this.pathMp3 = PathMp3;
    }
    public int getsTT() {
        return sTT;
    }

    public void setsTT(int sTT) {
        this.sTT = sTT;
    }
    public int getIdTopic() {
        return idTopic;
    }

    public void setIdTopic(int idTopic) {
        this.idTopic = idTopic;
    }

    public String getIdSubject() {
        return idSubject;
    }

    public void setIdSubject(String idSubject) {
        this.idSubject = idSubject;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getLinkMp3() {
        return linkMp3;
    }

    public void setLinkMp3(String linkMp3) {
        this.linkMp3 = linkMp3;
    }
}
