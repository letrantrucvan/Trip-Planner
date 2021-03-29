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
    private Integer rating_number;
    private Double rating_avg;

    private boolean archived_mode;
    private boolean isActive;

    public Tour(){}
    //get

    public String getTour_id() {
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
    public Integer getRating_number() {
        return rating_number;
    }
    public Double getRating_avg() {
        return rating_avg;
    }
    public boolean isArchived_mode() {
        return archived_mode;
    }
    public boolean isActive() {
        return isActive;
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
    public void setRating_number(Integer rating_number) {
        this.rating_number = rating_number;
    }
    public void setRating_avg(Double rating_avg) {
        this.rating_avg = rating_avg;
    }
    public void setTour_id(String tour_id) {
        this.tour_id = tour_id;
    }

    public void setArchived_mode(boolean archived_mode) {
        this.archived_mode = archived_mode;
    }
    public void setActive(boolean active) {
        isActive = active;
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
                ", archived_mode=" + archived_mode +
                ", isActive=" + isActive +
                '}';
    }
}
