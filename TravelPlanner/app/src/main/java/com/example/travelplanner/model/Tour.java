package com.example.travelplanner.model;

import java.util.ArrayList;
import java.util.Date;

public class Tour {
    private String tour_id;
    private String name;
    private String author_id;
    private String author_name;
    private String cover;
    private String des;
    private String publish_day;
    private Integer upvote_number;
    private Integer downvote_number;
    private Integer views_number;
    private Integer saved_number;
    private boolean archived_mode;
    private boolean isActive;

    private ArrayList<String> commentId;

    public Tour(){}
    //get
    public String getId() {
        return tour_id;
    }
    public String getName() {
        return name;
    }
    public String getAuthor_id() {
        return author_id;
    }
    public String getAuthor_name() {
        return author_name;
    }
    public String getCover() {
        return cover;
    }
    public String getDes() {
        return des;
    }
    public String getPublish_day() {
        return publish_day;
    }
    public Integer getUpvote_number() {
        return upvote_number;
    }
    public Integer getDownvote_number() {
        return downvote_number;
    }
    public Integer getViews_number() {
        return views_number;
    }
    public Integer getSaved_number() {
        return saved_number;
    }
    public boolean isArchived_mode() {
        return archived_mode;
    }
    public boolean isActive() {
        return isActive;
    }
    public ArrayList<String> getCommentId() {
        return commentId;
    }

    //set
    public void setId(String id) {
        this.tour_id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }
    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }
    public void setCover(String cover) {
        this.cover = cover;
    }
    public void setDes(String des) {
        this.des = des;
    }
    public void setPublish_day(String publish_day) {
        this.publish_day = publish_day;
    }
    public void setUpvote_number(Integer upvote_number) {
        this.upvote_number = upvote_number;
    }
    public void setDownvote_number(Integer downvote_number) {
        this.downvote_number = downvote_number;
    }
    public void setViews_number(Integer views_number) {
        this.views_number = views_number;
    }
    public void setSaved_number(Integer saved_number) {
        this.saved_number = saved_number;
    }
    public void setArchived_mode(boolean archived_mode) {
        this.archived_mode = archived_mode;
    }
    public void setActive(boolean active) {
        isActive = active;
    }
    public void setCommentId(ArrayList<String> commentId) {
        this.commentId = commentId;
    }

    static public void getHighlightedTour(){}
    static public void getNearByTour(){}

    @Override
    public String toString() {
        return "Tour{" +
                "tour_id='" + tour_id + '\'' +
                ", name='" + name + '\'' +
                ", author_id='" + author_id + '\'' +
                ", author_name='" + author_name + '\'' +
                ", cover='" + cover + '\'' +
                ", des='" + des + '\'' +
                ", publish_day='" + publish_day + '\'' +
                ", upvote_number=" + upvote_number +
                ", downvote_number=" + downvote_number +
                ", views_number=" + views_number +
                ", saved_number=" + saved_number +
                ", archived_mode=" + archived_mode +
                ", isActive=" + isActive +
                ", commentId=" + commentId +
                '}';
    }
}
