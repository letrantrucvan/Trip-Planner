package com.example.travelplanner.model;

import java.util.Date;

public class Notification {
    String id;
    String userID;
    String content;
    String link;
    String img;
    boolean seen;
    int type;
    long time;

    public Notification(){}

    public Notification(String userID, String content, String link, String img, int type) {
        this.userID = userID;
        this.content = content;
        this.link = link;
        this.img = img;
        this.type = type;
        this.seen = false;
        time = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
