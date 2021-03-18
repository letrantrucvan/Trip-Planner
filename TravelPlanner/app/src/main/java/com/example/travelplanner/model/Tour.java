package com.example.travelplanner.model;

import java.util.Date;

public class Tour {
    public String id;
    public String name;
    public String cover;
    public String des;
    public Date publish_date;
    public Integer upvote_number;
    public Integer downvote_number;
    public Integer views_number;
    public Integer saved_number;
    public boolean archived_mode;
    public boolean isActive;

    public Tour(){}
    static public void getHighlightedTour(){}
    static public void getNearByTour(){}


    public Integer getUpvote_number() {
        return upvote_number;
    }

    public Integer getDownvote_number() {
        return downvote_number;
    }

    public void setArchived_mode(boolean archived_mode) {
        this.archived_mode = archived_mode;
    }

    public String getCover() {
        return cover;
    }

    public String getName() {
        return name;
    }

    public String getDes() {
        return des;
    }
}